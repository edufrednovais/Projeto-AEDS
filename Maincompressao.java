import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.nio.file.*;

// =====================================================================
// CONTRATO COMUM AOS DOIS ALGORITMOS
// Trabalham arquivo -> arquivo (memória secundária), sem carregar tudo na RAM.
// =====================================================================
interface Compressor {
    void comprimir(String arquivoOrigem, String arquivoDestino) throws IOException;

    void descomprimir(String arquivoOrigem, String arquivoDestino) throws IOException;

    String nome(); // "Huffman" ou "LZW"
}

// =====================================================================
// BitOutput: acumula bits em um byte e grava no disco quando completa 8.
// Necessário porque Huffman e LZW geram códigos de tamanho variável.
// =====================================================================
class BitOutput {
    private final OutputStream out;
    private int buffer = 0; // bits acumulados
    private int nBits = 0; // quantos bits já estão no buffer

    public BitOutput(OutputStream out) {
        this.out = out;
    }

    // Grava os 'qtd' bits menos significativos de 'valor', do mais significativo ao
    // menos.
    public void escreverBits(int valor, int qtd) throws IOException {
        for (int i = qtd - 1; i >= 0; i--) {
            buffer = (buffer << 1) | ((valor >> i) & 1);
            nBits++;
            if (nBits == 8) { // byte completo: vai para o disco
                out.write(buffer);
                buffer = 0;
                nBits = 0;
            }
        }
    }

    // Completa o último byte com zeros (padding) e fecha.
    // Por isso o descompressor precisa saber quantos símbolos esperar.
    public void fechar() throws IOException {
        if (nBits > 0) {
            buffer <<= (8 - nBits);
            out.write(buffer);
        }
        out.close();
    }
}

// =====================================================================
// BitInput: lê o arquivo bit a bit (inverso do BitOutput).
// =====================================================================
class BitInput {
    private final InputStream in;
    private int buffer = 0;
    private int nBits = 0; // bits ainda não consumidos no buffer

    public BitInput(InputStream in) {
        this.in = in;
    }

    // Retorna 0 ou 1; retorna -1 no fim do arquivo.
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

    // Lê 'qtd' bits e monta um inteiro; -1 se o arquivo acabar antes.
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

// =====================================================================
// GerenciadorVersoes: padrão de nome nomeArquivo + nomeAlgoritmo + "Compressao"
// + X
// Ex.: jogosHuffmanCompressao1.dat
// =====================================================================
class GerenciadorVersoes {

    // Remove a extensão: "jogos.dat" -> "jogos"
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

    // Descobre a próxima versão livre olhando os arquivos existentes
    // de TODOS os algoritmos, para os dois usarem o mesmo X.
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
                        /* ignora nomes fora do padrão */ }
                }
            }
        }
        return maior + 1;
    }
}

// =====================================================================
// MENU PRINCIPAL
// =====================================================================
public class Maincompressao {

    static final String ARQUIVO = "jogos.dat";

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Compressor[] algoritmos = { new Huffman(), new LZW() };
        int op;
        do {
            System.out.println("\n1 - Comprimir base de dados");
            System.out.println("2 - Descomprimir uma versão");
            System.out.println("0 - Sair");
            op = Integer.parseInt(sc.nextLine());
            if (op == 1)
                comprimir(algoritmos);
            else if (op == 2) {
                System.out.print("Versão (X): ");
                descomprimir(algoritmos, Integer.parseInt(sc.nextLine()));
            }
        } while (op != 0);
    }

    // Comprime com os dois algoritmos, mede tempo e ganho, e compara.
    static void comprimir(Compressor[] algs) throws Exception {
        // Evita divisão por zero e erro de arquivo inexistente quando a base está vazia
        File f = new File(ARQUIVO);
        if (!f.exists() || f.length() == 0) {
            System.out.println("Base de dados vazia ou inexistente.");
            return;
        }
        long tamOriginal = new File(ARQUIVO).length();
        int versao = GerenciadorVersoes.proximaVersao(ARQUIVO, algs[0].nome(), algs[1].nome());
        double[] ganho = new double[2];
        long[] tempo = new long[2];

        for (int i = 0; i < 2; i++) {
            String saida = GerenciadorVersoes.nomeArquivo(ARQUIVO, algs[i].nome(), versao);
            long ini = System.nanoTime();
            algs[i].comprimir(ARQUIVO, saida);
            tempo[i] = System.nanoTime() - ini;
            long tamComp = new File(saida).length();
            // Positivo = ganho; negativo = perda (arquivo aumentou)
            ganho[i] = (1.0 - (double) tamComp / tamOriginal) * 100;
            System.out.printf("%s: %d -> %d bytes | %.2f%% | %.3f ms%n",
                    algs[i].nome(), tamOriginal, tamComp, ganho[i], tempo[i] / 1e6);
        }
        System.out.println("Melhor taxa de compressão: " + algs[ganho[0] >= ganho[1] ? 0 : 1].nome());
        System.out.println("Mais rápido: " + algs[tempo[0] <= tempo[1] ? 0 : 1].nome());
    }

    // Descomprime com os dois em arquivos temporários, confere se são idênticos,
    // e só então substitui o arquivo de dados.
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

        // Teste de corretude: os dois resultados precisam ser idênticos byte a byte.
        if (!iguais(temp[0], temp[1])) {
            System.out.println("ERRO: os dois algoritmos geraram resultados diferentes. "
                    + "Base de dados NÃO foi substituída.");
            return;
        }

        // Substitui a base pela versão descomprimida. O move com REPLACE_EXISTING
        // evita perder o jogos.dat caso a troca falhe no meio (como ocorria com delete
        // + renameTo)
        Files.move(Paths.get(temp[0]), Paths.get(ARQUIVO), StandardCopyOption.REPLACE_EXISTING);
        new File(temp[1]).delete(); // remove o temporário do outro algoritmo
        System.out.println("Base de dados restaurada.");
    }

    // Compara dois arquivos byte a byte, lendo em blocos (memória secundária).
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
