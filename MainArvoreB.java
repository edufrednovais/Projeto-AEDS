
import java.io.IOException;
import java.util.Scanner;

public class MainArvoreB {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int opcao = -1;

        while (opcao != 0) {

            System.out.println("\n==============================");
            System.out.println("       CRUD INDEXADO");
            System.out.println("==============================");
            System.out.println("1 - Criar registro");
            System.out.println("2 - Ler registro");
            System.out.println("3 - Atualizar registro");
            System.out.println("4 - Excluir registro");
            System.out.println("5 - Mostrar Arvore B");
            System.out.println("0 - Sair");
            System.out.println("==============================");
            System.out.print("Escolha uma opcao: ");

            opcao = scanner.nextInt();
            scanner.nextLine();

            try {

                // CREATE
                if (opcao == 1) {

                    System.out.println("\n===== CRIAR REGISTRO =====");

                    System.out.print("ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("CampFix: ");
                    String campFix = scanner.nextLine();

                    System.out.print("CampVariavel: ");
                    String campVariavel = scanner.nextLine();

                    System.out.print("Data: ");
                    String data = scanner.nextLine();

                    System.out.print("Lista: ");
                    String lista = scanner.nextLine();

                    System.out.print("Valor: ");
                    float valor = scanner.nextFloat();
                    scanner.nextLine();

                    Registro registro = new Registro(
                            id,
                            campFix,
                            campVariavel,
                            data,
                            lista,
                            valor
                    );

                    CRUDindex.CRIATE(registro);

                    System.out.println("\nRegistro criado com sucesso!");
                }


                // READ
                else if (opcao == 2) {

                    System.out.println("\n===== LER REGISTRO =====");

                    System.out.print("Digite o ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    Registro registro = CRUDindex.READ(id);

                    if (registro == null) {

                        System.out.println("\nRegistro nao encontrado.");

                    } else {

                        System.out.println("\nRegistro encontrado:");
                        System.out.println(registro.ToString());
                    }
                }


                // UPDATE
                else if (opcao == 3) {

                    System.out.println("\n===== ATUALIZAR REGISTRO =====");

                    System.out.print("Digite o ID do registro: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Novo CampFix: ");
                    String campFix = scanner.nextLine();

                    System.out.print("Novo CampVariavel: ");
                    String campVariavel = scanner.nextLine();

                    System.out.print("Nova Data: ");
                    String data = scanner.nextLine();

                    System.out.print("Nova Lista: ");
                    String lista = scanner.nextLine();

                    System.out.print("Novo Valor: ");
                    float valor = scanner.nextFloat();
                    scanner.nextLine();

                    Registro novoRegistro = new Registro(
                            id,
                            campFix,
                            campVariavel,
                            data,
                            lista,
                            valor
                    );

                    boolean sucesso =
                            CRUDindex.UPDATE(id, novoRegistro);

                    if (sucesso) {

                        System.out.println("\nRegistro atualizado com sucesso!");

                    } else {

                        System.out.println("\nRegistro nao encontrado.");
                    }
                }


                // DELETE
                else if (opcao == 4) {

                    System.out.println("\n===== EXCLUIR REGISTRO =====");

                    System.out.print("Digite o ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    boolean sucesso =
                            CRUDindex.excluir(id);

                    if (sucesso) {

                        System.out.println("\nRegistro excluido com sucesso!");

                    } else {

                        System.out.println("\nRegistro nao encontrado.");
                    }
                }


                // MOSTRAR ARVORE
                else if (opcao == 5) {

                    System.out.println("\n===== ARVORE B =====");

                    CRUDindex.getArvore().mostrar();
                }


                // SAIR
                else if (opcao == 0) {

                    System.out.println("\nPrograma encerrado.");

                }


                // OPCAO INVALIDA
                else {

                    System.out.println("\nOpcao invalida.");
                }

            } catch (IOException e) {

                System.out.println("\nErro ao acessar o arquivo:");
                System.out.println(e.getMessage());
            }
        }

        scanner.close();
    }
}

