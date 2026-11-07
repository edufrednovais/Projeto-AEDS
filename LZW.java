import java.io.*;
import java.util.HashMap;

public class LZW implements Compressor {

    // DECISÃO: códigos de largura fixa de 12 bits => dicionário de 4096 entradas.
    private static final int BITS_CODIGO = 12;
    private static final int MAX_ENTRADAS = 1 << BITS_CODIGO; // 4096

    // ------------------------------------------------------------------
    // COMPRESSÃO
    // Formato do arquivo gerado: [long tamanhoOriginal][códigos de 12 bits...]
    // ------------------------------------------------------------------
    @Override
    public void comprimir(String origem, String destino) throws IOException {
        long total = new File(origem).length();

        try (InputStream in = new BufferedInputStream(new FileInputStream(origem));
                DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(destino)))) {

            dos.writeLong(total);
            if (total == 0)
                return; // arquivo vazio: só o cabeçalho

            BitOutput bits = new BitOutput(dos);

            // DICIONÁRIO INICIAL: as 256 sequências de 1 byte (códigos 0..255) são
            // implícitas. Só guardamos as novas (a partir do código 256).
            // Chave = (código do prefixo * 256) + próximo byte; valor = código novo.
            // Isso evita criar Strings/arrays para cada sequência.
            HashMap<Integer, Integer> dic = new HashMap<>();
            int proximo = 256; // próximo código livre

            int prefixo = in.read(); // sequência atual (representada pelo seu código)
            int c;
            while ((c = in.read()) != -1) {
                int chave = (prefixo << 8) | c;
                Integer codigo = dic.get(chave);

                if (codigo != null) {
                    // prefixo + c já existe: estende a sequência atual
                    prefixo = codigo;
                } else {
                    // não existe: emite o código do prefixo...
                    bits.escreverBits(prefixo, BITS_CODIGO);
                    // ...e cadastra a nova sequência, se ainda houver espaço
                    if (proximo < MAX_ENTRADAS) {
                        dic.put(chave, proximo++);
                    } else {
                        // Dicionário cheio: REINICIA (o descompressor faz o mesmo no mesmo ponto)
                        dic.clear();
                        proximo = 256;
                    }
                    prefixo = c; // recomeça a sequência a partir do byte atual
                }
            }
            // Emite a última sequência pendente
            bits.escreverBits(prefixo, BITS_CODIGO);
            bits.fechar();
        }
    }

    // ------------------------------------------------------------------
    // DESCOMPRESSÃO
    // O dicionário não está no arquivo: é reconstruído conforme os códigos são
    // lidos.
    // ------------------------------------------------------------------
    @Override
    public void descomprimir(String origem, String destino) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(origem)));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(destino))) {

            long total = dis.readLong();
            if (total == 0)
                return;

            BitInput bits = new BitInput(dis);

            // Dicionário: posição = código, conteúdo = sequência de bytes
            byte[][] dic = new byte[MAX_ENTRADAS][];
            int proximo = iniciarDicionario(dic);

            byte[] anterior = null; // sequência do código anterior (null = início ou pós-reinício)
            long escritos = 0;

            while (escritos < total) {
                int codigo = bits.lerBits(BITS_CODIGO);
                if (codigo == -1)
                    throw new IOException("Arquivo comprimido corrompido.");

                byte[] entrada;
                if (anterior == null) {
                    // Primeiro código (ou logo após reinício): tem que existir no dicionário
                    if (dic[codigo] == null)
                        throw new IOException("Código inválido.");
                    entrada = dic[codigo];
                } else {
                    if (codigo < proximo) {
                        entrada = dic[codigo];
                    } else if (codigo == proximo) {
                        // CASO ESPECIAL (cScSc): o código ainda não existe no dicionário,
                        // pois o compressor o criou e usou logo em seguida.
                        // Ele só pode ser: anterior + primeiro byte de anterior.
                        entrada = concatenar(anterior, anterior[0]);
                    } else {
                        throw new IOException("Código inválido.");
                    }
                    // Cadastra a sequência que o compressor criou no passo anterior:
                    // anterior + primeiro byte da entrada atual
                    dic[proximo++] = concatenar(anterior, entrada[0]);
                }

                out.write(entrada);
                escritos += entrada.length;
                anterior = entrada;

                // Dicionário cheio: o compressor reiniciou neste mesmo ponto, então reiniciamos
                // também
                if (proximo == MAX_ENTRADAS) {
                    proximo = iniciarDicionario(dic);
                    anterior = null;
                }
            }
        }
    }

    // Preenche as 256 entradas iniciais (um byte cada) e retorna o próximo código
    // livre (256)
    private static int iniciarDicionario(byte[][] dic) {
        for (int i = 0; i < 256; i++)
            dic[i] = new byte[] { (byte) i };
        return 256;
    }

    // Retorna uma cópia de 'seq' com 'b' adicionado no final
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