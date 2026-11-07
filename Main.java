import java.util.Scanner;

// Menu principal do sistema: reúne as operações do CRUD e a compressão de dados.
// Todas as classes devem estar na mesma pasta e sem "package".
public class Main {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        // Os dois algoritmos de compressão são usados juntos em cada operação
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
                case 1: // CREATE
                    Registro novo = lerRegistro(sc);
                    CRUDIndex.CRIATE(novo);
                    System.out.println("Registro criado.");
                    break;

                case 2: // READ
                    System.out.print("ID: ");
                    Registro r = CRUDIndex.READ(lerInt(sc));
                    System.out.println(r == null ? "Registro não encontrado." : r.ToString());
                    break;

                case 3: // UPDATE
                    System.out.print("ID do registro a atualizar: ");
                    int idAtualizar = lerInt(sc);
                    Registro atualizado = lerRegistro(sc);
                    System.out.println(CRUDIndex.UPDATE(idAtualizar, atualizado)
                            ? "Registro atualizado."
                            : "Registro não encontrado.");
                    break;

                case 4: // DELETE
                    System.out.print("ID: ");
                    System.out.println(CRUDIndex.excluir(lerInt(sc))
                            ? "Registro excluído."
                            : "Registro não encontrado.");
                    break;

                case 5: // Compressão: gera uma versão nova com cada algoritmo
                    Maincompressao.comprimir(algoritmos);
                    break;

                case 6: // Descompressão: restaura o jogos.dat de uma versão X
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

    // Lê um inteiro da linha digitada; evita travar se o usuário digitar texto
    private static int lerInt(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Pede os campos do registro ao usuário (ordem igual à do construtor de
    // Registro)
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