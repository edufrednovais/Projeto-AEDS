import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.text.DecimalFormat;

interface Compressor {
    void comprimir(String arquivoOrigem, String arquivoDestino) throws IOException;

    void descomprimir(String arquivoOrigem, String arquivoDestino) throws IOException;

    String nome();
}

class BitOutput {
    private final OutputStream out;
    private int buffer = 0;
    private int nBits = 0;

    public BitOutput(OutputStream out) {
        this.out = out;
    }

    public void escreverBits(int valor, int qtd) throws IOException {
        for (int i = qtd - 1; i >= 0; i--) {
            buffer = (buffer << 1) | ((valor >> i) & 1);
            nBits++;
            if (nBits == 8) {
                out.write(buffer);
                buffer = 0;
                nBits = 0;
            }
        }
    }

    public void fechar() throws IOException {
        if (nBits > 0) {
            buffer <<= (8 - nBits);
            out.write(buffer);
        }
        out.close();
    }
}

class BitInput {
    private final InputStream in;
    private int buffer = 0;
    private int nBits = 0;

    public BitInput(InputStream in) {
        this.in = in;
    }

    public int lerBit() throws IOException {
        if (nBits == 0) {
            buffer = in.read();
            if (buffer == -1)
                return -1;
            nBits = 8;
        }
        nBits--;
        return (buffer >> nBits) & 1;
    }

    public int lerBits(int qtd) throws IOException {
        int valor = 0;
        for (int i = 0; i < qtd; i++) {
            int b = lerBit();
            if (b == -1)
                return -1;
            valor = (valor << 1) | b;
        }
        return valor;
    }
}

class GerenciadorVersoes {

    private static String base(String arquivo) {
        int p = arquivo.lastIndexOf('.');
        return p == -1 ? arquivo : arquivo.substring(0, p);
    }

    private static String extensao(String arquivo) {
        int p = arquivo.lastIndexOf('.');
        return p == -1 ? "" : arquivo.substring(p);
    }

    public static String nomeArquivo(String original, String algoritmo, int versao) {
        return base(original) + algoritmo + "Compressao" + versao + extensao(original);
    }

    public static int proximaVersao(String original, String... algoritmos) {
        int maior = 0;
        File[] arquivos = new File(".").listFiles();
        if (arquivos == null)
            return 1;
        for (String alg : algoritmos) {
            String prefixo = base(original) + alg + "Compressao";
            for (File f : arquivos) {
                String n = f.getName();
                if (n.startsWith(prefixo) && n.endsWith(extensao(original))) {
                    String num = n.substring(prefixo.length(), n.length() - extensao(original).length());
                    try {
                        maior = Math.max(maior, Integer.parseInt(num));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        return maior + 1;
    }
}

class Huffman implements Compressor {

    private static class No implements Comparable<No> {
        int simbolo;
        long freq;
        int menor;
        No esq, dir;

        No(int simbolo, long freq) {
            this.simbolo = simbolo;
            this.freq = freq;
            this.menor = simbolo;
        }

        No(No esq, No dir) {
            this.simbolo = -1;
            this.esq = esq;
            this.dir = dir;
            this.freq = esq.freq + dir.freq;
            this.menor = Math.min(esq.menor, dir.menor);
        }

        boolean folha() {
            return esq == null && dir == null;
        }

        @Override
        public int compareTo(No o) {
            if (freq != o.freq)
                return Long.compare(freq, o.freq);
            return Integer.compare(menor, o.menor);
        }
    }

    private static No construirArvore(long[] freq) {
        PriorityQueue<No> fila = new PriorityQueue<>();
        for (int i = 0; i < 256; i++) {
            if (freq[i] > 0)
                fila.add(new No(i, freq[i]));
        }
        if (fila.isEmpty())
            return null;

        while (fila.size() > 1) {
            No a = fila.poll();
            No b = fila.poll();
            fila.add(new No(a, b));
        }
        return fila.poll();
    }

    private static void gerarCodigos(No no, String prefixo, String[] codigos) {
        if (no.folha()) {
            codigos[no.simbolo] = prefixo.isEmpty() ? "0" : prefixo;
            return;
        }
        gerarCodigos(no.esq, prefixo + "0", codigos);
        gerarCodigos(no.dir, prefixo + "1", codigos);
    }

    @Override
    public void comprimir(String origem, String destino) throws IOException {
        long[] freq = new long[256];
        long total = 0;
        try (InputStream in = new BufferedInputStream(new FileInputStream(origem))) {
            int b;
            while ((b = in.read()) != -1) {
                freq[b]++;
                total++;
            }
        }
        if (total > Integer.MAX_VALUE) {
            throw new IOException("Arquivo grande demais para esta implementação.");
        }

        No raiz = construirArvore(freq);
        String[] codigos = new String[256];
        if (raiz != null)
            gerarCodigos(raiz, "", codigos);

        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(destino)))) {

            dos.writeLong(total);
            int qtd = 0;
            for (int i = 0; i < 256; i++)
                if (freq[i] > 0)
                    qtd++;
            dos.writeShort(qtd);
            for (int i = 0; i < 256; i++) {
                if (freq[i] > 0) {
                    dos.writeByte(i);
                    dos.writeInt((int) freq[i]);
                }
            }

            BitOutput bits = new BitOutput(dos);
            try (InputStream in = new BufferedInputStream(new FileInputStream(origem))) {
                int b;
                while ((b = in.read()) != -1) {
                    String codigo = codigos[b];
                    for (int i = 0; i < codigo.length(); i++) {
                        bits.escreverBits(codigo.charAt(i) - '0', 1);
                    }
                }
            }
            bits.fechar();
        }
    }

    @Override
    public void descomprimir(String origem, String destino) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(origem)));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(destino))) {

            long total = dis.readLong();
            int qtd = dis.readUnsignedShort();
            long[] freq = new long[256];
            for (int i = 0; i < qtd; i++) {
                int simbolo = dis.readUnsignedByte();
                freq[simbolo] = dis.readInt();
            }

            No raiz = construirArvore(freq);
            if (raiz == null)
                return;

            if (raiz.folha()) {
                for (long i = 0; i < total; i++)
                    out.write(raiz.simbolo);
                return;
            }

            BitInput bits = new BitInput(dis);
            No atual = raiz;
            long escritos = 0;
            while (escritos < total) {
                int bit = bits.lerBit();
                if (bit == -1)
                    throw new IOException("Arquivo comprimido corrompido.");
                atual = (bit == 0) ? atual.esq : atual.dir;
                if (atual.folha()) {
                    out.write(atual.simbolo);
                    escritos++;
                    atual = raiz;
                }
            }
        }
    }

    @Override
    public String nome() {
        return "Huffman";
    }
}

class LZW implements Compressor {

    private static final int BITS_CODIGO = 12;
    private static final int MAX_ENTRADAS = 1 << BITS_CODIGO;

    @Override
    public void comprimir(String origem, String destino) throws IOException {
        long total = new File(origem).length();

        try (InputStream in = new BufferedInputStream(new FileInputStream(origem));
                DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(destino)))) {

            dos.writeLong(total);
            if (total == 0)
                return;

            BitOutput bits = new BitOutput(dos);

            HashMap<Integer, Integer> dic = new HashMap<>();
            int proximo = 256;

            int prefixo = in.read();
            int c;
            while ((c = in.read()) != -1) {
                int chave = (prefixo << 8) | c;
                Integer codigo = dic.get(chave);

                if (codigo != null) {
                    prefixo = codigo;
                } else {
                    bits.escreverBits(prefixo, BITS_CODIGO);
                    if (proximo < MAX_ENTRADAS) {
                        dic.put(chave, proximo++);
                    } else {
                        dic.clear();
                        proximo = 256;
                    }
                    prefixo = c;
                }
            }
            bits.escreverBits(prefixo, BITS_CODIGO);
            bits.fechar();
        }
    }

    @Override
    public void descomprimir(String origem, String destino) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(origem)));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(destino))) {

            long total = dis.readLong();
            if (total == 0)
                return;

            BitInput bits = new BitInput(dis);

            byte[][] dic = new byte[MAX_ENTRADAS][];
            int proximo = iniciarDicionario(dic);

            byte[] anterior = null;
            long escritos = 0;

            while (escritos < total) {
                int codigo = bits.lerBits(BITS_CODIGO);
                if (codigo == -1)
                    throw new IOException("Arquivo comprimido corrompido.");

                byte[] entrada;
                if (anterior == null) {
                    if (dic[codigo] == null)
                        throw new IOException("Código inválido.");
                    entrada = dic[codigo];
                } else {
                    if (codigo < proximo) {
                        entrada = dic[codigo];
                    } else if (codigo == proximo) {
                        entrada = concatenar(anterior, anterior[0]);
                    } else {
                        throw new IOException("Código inválido.");
                    }
                    dic[proximo++] = concatenar(anterior, entrada[0]);
                }

                out.write(entrada);
                escritos += entrada.length;
                anterior = entrada;

                if (proximo == MAX_ENTRADAS) {
                    proximo = iniciarDicionario(dic);
                    anterior = null;
                }
            }
        }
    }

    private static int iniciarDicionario(byte[][] dic) {
        for (int i = 0; i < 256; i++)
            dic[i] = new byte[] { (byte) i };
        return 256;
    }

    private static byte[] concatenar(byte[] seq, byte b) {
        byte[] novo = new byte[seq.length + 1];
        System.arraycopy(seq, 0, novo, 0, seq.length);
        novo[seq.length] = b;
        return novo;
    }

    @Override
    public String nome() {
        return "LZW";
    }
}

class Maincompressao {

    static final String ARQUIVO = "jogos.dat";

    static void comprimir(Compressor[] algs) throws Exception {
        File f = new File(ARQUIVO);
        if (!f.exists() || f.length() == 0) {
            System.out.println("Base de dados vazia ou inexistente.");
            return;
        }
        long tamOriginal = f.length();
        int versao = GerenciadorVersoes.proximaVersao(ARQUIVO, algs[0].nome(), algs[1].nome());
        double[] ganho = new double[2];
        long[] tempo = new long[2];

        for (int i = 0; i < 2; i++) {
            String saida = GerenciadorVersoes.nomeArquivo(ARQUIVO, algs[i].nome(), versao);
            long ini = System.nanoTime();
            algs[i].comprimir(ARQUIVO, saida);
            tempo[i] = System.nanoTime() - ini;
            long tamComp = new File(saida).length();
            ganho[i] = (1.0 - (double) tamComp / tamOriginal) * 100;
            System.out.printf("%s: %d -> %d bytes | %.2f%% | %.3f ms%n",
                    algs[i].nome(), tamOriginal, tamComp, ganho[i], tempo[i] / 1e6);
        }
        System.out.println("Melhor taxa de compressão: " + algs[ganho[0] >= ganho[1] ? 0 : 1].nome());
        System.out.println("Mais rápido: " + algs[tempo[0] <= tempo[1] ? 0 : 1].nome());
    }

    static void descomprimir(Compressor[] algs, int versao) throws Exception {
        long[] tempo = new long[2];
        String[] temp = new String[2];
        for (int i = 0; i < 2; i++) {
            String entrada = GerenciadorVersoes.nomeArquivo(ARQUIVO, algs[i].nome(), versao);
            if (!new File(entrada).exists()) {
                System.out.println("Versão não encontrada: " + entrada);
                return;
            }
            temp[i] = "tmp_" + algs[i].nome() + ".dat";
            long ini = System.nanoTime();
            algs[i].descomprimir(entrada, temp[i]);
            tempo[i] = System.nanoTime() - ini;
            System.out.printf("%s: %.3f ms%n", algs[i].nome(), tempo[i] / 1e6);
        }
        System.out.println("Mais rápido: " + algs[tempo[0] <= tempo[1] ? 0 : 1].nome());

        if (!iguais(temp[0], temp[1])) {
            System.out.println("ERRO: os dois algoritmos geraram resultados diferentes. "
                    + "Base de dados NAO foi substituida.");
            return;
        }

        Files.move(Paths.get(temp[0]), Paths.get(ARQUIVO), StandardCopyOption.REPLACE_EXISTING);
        new File(temp[1]).delete();
        System.out.println("Base de dados restaurada.");
    }

    static boolean iguais(String a, String b) throws IOException {
        if (new File(a).length() != new File(b).length())
            return false;
        try (InputStream ia = new BufferedInputStream(new FileInputStream(a));
                InputStream ib = new BufferedInputStream(new FileInputStream(b))) {
            int x;
            while ((x = ia.read()) != -1) {
                if (x != ib.read())
                    return false;
            }
        }
        return true;
    }
}

class Registro {

    int id;
    byte lapide;
    String campFix;
    String campVariavel;
    String data;
    String lista;
    float valor;

    DecimalFormat df = new DecimalFormat("#,##0.00");

    Registro(int id, String campFix, String campVariavel, String data, String lista, float valor) {
        this.id = id;
        this.lapide = 0;
        this.campFix = campFix;
        this.campVariavel = campVariavel;
        this.data = data;
        this.lista = lista;
        this.valor = valor;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeByte(lapide);
        dos.writeUTF(campFix);
        dos.writeUTF(campVariavel);
        dos.writeUTF(data);
        dos.writeUTF(lista);
        dos.writeFloat(valor);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        lapide = dis.readByte();
        campFix = dis.readUTF();
        campVariavel = dis.readUTF();
        data = dis.readUTF();
        lista = dis.readUTF();
        valor = dis.readFloat();
    }

    public String ToString() {
        return "\nId: " + id +
                "\nCampFix: " + campFix +
                "\nCampVariavel: " + campVariavel +
                "\nData: " + data +
                "\nLista: " + lista +
                "\nValor: R$ " + df.format(valor);
    }
}

class CRUDIndex {

    private static final String ARQUIVO = "jogos.dat";

    private static Registro lerRegistro(RandomAccessFile arq) throws IOException {
        int tamanho = arq.readInt();
        byte[] dados = new byte[tamanho];
        arq.readFully(dados);
        Registro r = new Registro(0, "", "", "", "", 0);
        r.fromByteArray(dados);
        return r;
    }

    private static long buscarPosicao(int id) throws IOException {
        File f = new File(ARQUIVO);
        if (!f.exists())
            return -1;

        try (RandomAccessFile arq = new RandomAccessFile(f, "r")) {
            while (arq.getFilePointer() < arq.length()) {
                long pos = arq.getFilePointer();
                Registro r = lerRegistro(arq);
                if (r.lapide == 0 && r.id == id)
                    return pos;
            }
        }
        return -1;
    }

    public static void CRIATE(Registro registro) throws IOException {
        try (RandomAccessFile arq = new RandomAccessFile(ARQUIVO, "rw")) {
            arq.seek(arq.length());
            registro.lapide = 0;
            byte[] dados = registro.toByteArray();
            arq.writeInt(dados.length);
            arq.write(dados);
        }
    }

    public static Registro READ(int id) throws IOException {
        long pos = buscarPosicao(id);
        if (pos == -1)
            return null;

        try (RandomAccessFile arq = new RandomAccessFile(ARQUIVO, "r")) {
            arq.seek(pos);
            return lerRegistro(arq);
        }
    }

    public static boolean UPDATE(int id, Registro novoRegistro) throws IOException {
        long pos = buscarPosicao(id);
        if (pos == -1)
            return false;

        try (RandomAccessFile arq = new RandomAccessFile(ARQUIVO, "rw")) {
            arq.seek(pos);
            int tamanhoAntigo = arq.readInt();
            byte[] dadosAntigos = new byte[tamanhoAntigo];
            arq.readFully(dadosAntigos);

            novoRegistro.id = id;
            novoRegistro.lapide = 0;
            byte[] novosDados = novoRegistro.toByteArray();

            if (novosDados.length == tamanhoAntigo) {
                arq.seek(pos);
                arq.writeInt(novosDados.length);
                arq.write(novosDados);
            } else {
                Registro antigo = new Registro(0, "", "", "", "", 0);
                antigo.fromByteArray(dadosAntigos);
                antigo.lapide = 1;
                byte[] apagado = antigo.toByteArray();
                arq.seek(pos);
                arq.writeInt(apagado.length);
                arq.write(apagado);

                arq.seek(arq.length());
                arq.writeInt(novosDados.length);
                arq.write(novosDados);
            }
        }
        return true;
    }

    public static boolean excluir(int id) throws IOException {
        long pos = buscarPosicao(id);
        if (pos == -1)
            return false;

        try (RandomAccessFile arq = new RandomAccessFile(ARQUIVO, "rw")) {
            arq.seek(pos);
            Registro r = lerRegistro(arq);
            r.lapide = 1;
            byte[] dados = r.toByteArray();
            arq.seek(pos);
            arq.writeInt(dados.length);
            arq.write(dados);
        }
        return true;
    }
}

public class HufLzw {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Compressor[] algoritmos = { new Huffman(), new LZW() };
        int op;

        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1 - Criar registro");
            System.out.println("2 - Buscar registro");
            System.out.println("3 - Atualizar registro");
            System.out.println("4 - Excluir registro");
            System.out.println("5 - Comprimir base de dados (Huffman e LZW)");
            System.out.println("6 - Descomprimir uma versão");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            op = lerInt(sc);

            switch (op) {
                case 1:
                    Registro novo = lerRegistro(sc);
                    CRUDIndex.CRIATE(novo);
                    System.out.println("Registro criado.");
                    break;

                case 2:
                    System.out.print("ID: ");
                    Registro r = CRUDIndex.READ(lerInt(sc));
                    System.out.println(r == null ? "Registro não encontrado." : r.ToString());
                    break;

                case 3:
                    System.out.print("ID do registro a atualizar: ");
                    int idAtualizar = lerInt(sc);
                    Registro atualizado = lerRegistro(sc);
                    System.out.println(CRUDIndex.UPDATE(idAtualizar, atualizado)
                            ? "Registro atualizado."
                            : "Registro não encontrado.");
                    break;

                case 4:
                    System.out.print("ID: ");
                    System.out.println(CRUDIndex.excluir(lerInt(sc))
                            ? "Registro excluído."
                            : "Registro não encontrado.");
                    break;

                case 5:
                    Maincompressao.comprimir(algoritmos);
                    break;

                case 6:
                    System.out.print("Versão (X): ");
                    Maincompressao.descomprimir(algoritmos, lerInt(sc));
                    break;

                case 0:
                    System.out.println("Encerrando.");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }
        } while (op != 0);
    }

    private static int lerInt(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Registro lerRegistro(Scanner sc) {
        System.out.print("ID: ");
        int id = lerInt(sc);
        System.out.print("Campo fixo: ");
        String campFix = sc.nextLine();
        System.out.print("Campo variável: ");
        String campVariavel = sc.nextLine();
        System.out.print("Data: ");
        String data = sc.nextLine();
        System.out.print("Lista: ");
        String lista = sc.nextLine();
        System.out.print("Valor: ");
        float valor = Float.parseFloat(sc.nextLine().trim().replace(',', '.'));
        return new Registro(id, campFix, campVariavel, data, lista, valor);
    }
}
