import java.io.*;
import java.util.PriorityQueue;

public class Huffman implements Compressor {

    // ------------------------------------------------------------------
    // Nó da árvore de Huffman. Pode ser folha (guarda um byte) ou nó interno.
    // ------------------------------------------------------------------
    private static class No implements Comparable<No> {
        int simbolo; // byte (0..255) se for folha; -1 se for nó interno
        long freq; // frequência (soma das frequências dos filhos, se interno)
        int menor; // menor símbolo contido na subárvore (usado no DESEMPATE)
        No esq, dir;

        // Construtor de folha
        No(int simbolo, long freq) {
            this.simbolo = simbolo;
            this.freq = freq;
            this.menor = simbolo;
        }

        // Construtor de nó interno: junta dois nós
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

        // Ordena por frequência; se empatar, pelo menor símbolo contido.
        // Dois nós da fila nunca compartilham símbolos, então "menor" nunca empata:
        // a ordem é total e a árvore é sempre a mesma para as mesmas frequências.
        @Override
        public int compareTo(No o) {
            if (freq != o.freq)
                return Long.compare(freq, o.freq);
            return Integer.compare(menor, o.menor);
        }
    }

    // Monta a árvore a partir das frequências (usado na compressão E na
    // descompressão).
    // Retorna null se o arquivo é vazio (nenhuma frequência).
    private static No construirArvore(long[] freq) {
        PriorityQueue<No> fila = new PriorityQueue<>();
        for (int i = 0; i < 256; i++) {
            if (freq[i] > 0)
                fila.add(new No(i, freq[i]));
        }
        if (fila.isEmpty())
            return null;

        // Enquanto houver mais de um nó, junta os dois de menor frequência
        while (fila.size() > 1) {
            No a = fila.poll(); // menor -> filho esquerdo (bit 0)
            No b = fila.poll(); // segundo menor -> filho direito (bit 1)
            fila.add(new No(a, b));
        }
        return fila.poll();
    }

    // Percorre a árvore gerando o código de cada byte (esquerda = '0', direita =
    // '1').
    // Usamos String porque, no pior caso, um código pode passar de 32 bits.
    private static void gerarCodigos(No no, String prefixo, String[] codigos) {
        if (no.folha()) {
            // Caso especial: árvore com uma única folha não tem prefixo; usamos "0"
            codigos[no.simbolo] = prefixo.isEmpty() ? "0" : prefixo;
            return;
        }
        gerarCodigos(no.esq, prefixo + "0", codigos);
        gerarCodigos(no.dir, prefixo + "1", codigos);
    }

    // ------------------------------------------------------------------
    // COMPRESSÃO
    // Formato do arquivo gerado:
    // [long tamanhoOriginal][short qtdSimbolos][(byte simbolo, int freq) x
    // qtd][bits]
    // ------------------------------------------------------------------
    @Override
    public void comprimir(String origem, String destino) throws IOException {
        // PASSADA 1: conta a frequência de cada byte
        long[] freq = new long[256];
        long total = 0;
        try (InputStream in = new BufferedInputStream(new FileInputStream(origem))) {
            int b;
            while ((b = in.read()) != -1) {
                freq[b]++;
                total++;
            }
        }
        // Frequências são gravadas como int (4 bytes); limite de ~2 GB por arquivo
        if (total > Integer.MAX_VALUE) {
            throw new IOException("Arquivo grande demais para esta implementação.");
        }

        // Monta a árvore e a tabela de códigos
        No raiz = construirArvore(freq);
        String[] codigos = new String[256];
        if (raiz != null)
            gerarCodigos(raiz, "", codigos);

        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(destino)))) {

            // CABEÇALHO: tamanho original + tabela de frequências (só símbolos que existem)
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

            // PASSADA 2: grava o código de cada byte, bit a bit
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
            bits.fechar(); // completa o último byte com padding e fecha o arquivo
        }
    }

    // ------------------------------------------------------------------
    // DESCOMPRESSÃO
    // ------------------------------------------------------------------
    @Override
    public void descomprimir(String origem, String destino) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(origem)));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(destino))) {

            // Lê o cabeçalho
            long total = dis.readLong();
            int qtd = dis.readUnsignedShort();
            long[] freq = new long[256];
            for (int i = 0; i < qtd; i++) {
                int simbolo = dis.readUnsignedByte();
                freq[simbolo] = dis.readInt();
            }

            // Reconstrói a MESMA árvore da compressão (mesmo método, mesmo desempate)
            No raiz = construirArvore(freq);
            if (raiz == null)
                return; // arquivo original vazio

            // Caso especial: um único símbolo distinto. Não há bits úteis para ler,
            // basta repetir o símbolo 'total' vezes.
            if (raiz.folha()) {
                for (long i = 0; i < total; i++)
                    out.write(raiz.simbolo);
                return;
            }

            // Caminha na árvore bit a bit: 0 = esquerda, 1 = direita.
            // Ao chegar em uma folha, grava o byte e volta para a raiz.
            BitInput bits = new BitInput(dis);
            No atual = raiz;
            long escritos = 0;
            // Para ao gravar 'total' bytes: os bits de padding do último byte são ignorados
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
