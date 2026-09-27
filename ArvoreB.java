
public class ArvoreB {
    // Ordem da arvore
    // Ordem 4
    // maximo de 3 chaves por no
    // maximo de 4 filhos
    private static final int ORDEM = 4;

    private static final int MAX_CHAVES = ORDEM - 1;

    // classe no

    class No {

        int[] ids;

        long[] posicoes;

        No[] filhos;

        int quantidade;

        boolean folha;
    
        // Construtires

        No(boolean folha) {

            this.folha = folha;

            ids = new int[MAX_CHAVES];

            posicoes = new long[MAX_CHAVES];

            filhos = new No[ORDEM];

            quantidade = 0;
        }
    }

    // raiz
    private No raiz;

    //Construtor da raiz

    public ArvoreB() {

        raiz = null;
    }

    // Busca, no caso o metodo de gatilho pro recursivo
    // Recebe um ID e retorna a posicao dele no jogos.dat.
    // Se nao encontrar:
    // retorna -1.

    public long buscar(int id) {

        if (raiz == null) {

            return -1;
        }

        return buscar(raiz, id);
    }


    
    // Busca recursiva

    private long buscar(No no, int id) {

        int i = 0;


        // Procura a posicao correta dentro do no.

        while (i < no.quantidade && id > no.ids[i]) {

            i++;
        }


        // Encontrou o ID.

        if (i < no.quantidade && id == no.ids[i]) {

            return no.posicoes[i];
        }


        // Se chegou em uma folha, significa que nao encontrou.
        

        if (no.folha) {

            return -1;
        }


        // Continua procurando no filho correto.

        return buscar(no.filhos[i], id);
    }

    // INSERÇÃO
   
    public void inserir(int id, long posicao) {

        // Se a árvore ainda está vazia.

        if (raiz == null) {

            raiz = new No(true);

            raiz.ids[0] = id;

            raiz.posicoes[0] = posicao;

            raiz.quantidade = 1;

            return;
        }


        // Se o ID ja existe,atualiza apenas a posição.
     

        if (buscar(id) != -1) {

            atualizar(id, posicao);

            return;
        }


        // Se a raiz esta cheia, precisamos criar uma nova raiz.
        

        if (raiz.quantidade == MAX_CHAVES) {

            No novaRaiz = new No(false);

            novaRaiz.filhos[0] = raiz;

            dividirFilho(novaRaiz, 0);

            raiz = novaRaiz;
        }

        // Insere

        inserirNaoCheio(raiz, id, posicao);
    }

    //Inserir em No nao cheio
    

    private void inserirNaoCheio(No no, int id, long posicao) {

        int i = no.quantidade - 1;

        // Caso 1 No folha
        
        if (no.folha) {

            // Desloca as chaves maiores
            // para abrir espaço.

            while (i >= 0 && id < no.ids[i]) {

                no.ids[i + 1] = no.ids[i];

                no.posicoes[i + 1] = no.posicoes[i];

                i--;
            }


            // Insere a nova chave.

            no.ids[i + 1] = id;

            no.posicoes[i + 1] = posicao;

            no.quantidade++;

            return;
        }


       
        // CASO 1 No internoo

        // Descobre em qual filho devemos entrar.

        while (i >= 0 && id < no.ids[i]) {
            i--;
        }

        i++;


        // Se o filho estiver cheio,
        // precisamos dividi-lo antes de entrar.

        if (no.filhos[i].quantidade == MAX_CHAVES) {

            dividirFilho(no, i);

            // Depois da divisao,
            // precisamos descobrir para qual lado
            // devemos continuar.

            if (id > no.ids[i]) {

                i++;
            }
        }

        inserirNaoCheio(no.filhos[i], id, posicao);
    }

    // Divide o filho pra que ocorra a fraqmentacao sem lascar o pai e o balanceamento

    private void dividirFilho(No pai, int indice) {

        // Filho que esta cheio.

        No cheio = pai.filhos[indice];

        // Cria o novo no que receberá
        // a segunda metade.

        No novo = new No(cheio.folha);

        // Para ordem 4, o filho cheio possui 3 chaves.
        // Tipo exemplo:
        // [10 | 20 | 30]
        // O 20 sobe.
        // [10]     [30]
        //       20
        // essa e a logica

        int meio = 1;


        // Guarda a chave que sera promovida.

        int idPromovido = cheio.ids[meio];

        long posicaoPromovida = cheio.posicoes[meio];


        // Copia a chave da direita.

        novo.ids[0] = cheio.ids[2];

        novo.posicoes[0] = cheio.posicoes[2];

        novo.quantidade = 1;


        // Se nao for folha,
        // tambem precisamos mover o filho.

        if (!cheio.folha) {

            novo.filhos[0] = cheio.filhos[2];

            novo.filhos[1] = cheio.filhos[3];
        }


        // O no antigo fica somente com
        // a chave da esquerda.

        cheio.quantidade = 1;

        // Abrir espacos dos filhos pro pai
       
        int j = pai.quantidade;

        while (j >= indice + 1) {

            pai.filhos[j + 1] = pai.filhos[j];

            j--;
        }


        // Coloca o novo nó na posição correta.

        pai.filhos[indice + 1] = novo;


       
        // ABRIR ESPAÇO NAS CHAVES DO PAI
       

        j = pai.quantidade - 1;


        while (j >= indice) {

            pai.ids[j + 1] = pai.ids[j];

            pai.posicoes[j + 1] = pai.posicoes[j];

            j--;
        }

        // Promove a chave 

        pai.ids[indice] = idPromovido;

        pai.posicoes[indice] = posicaoPromovida;

        pai.quantidade++;
    }

    // Atualizar
    // Usado principalmente quando o UPDATE do CRUD
    // coloca o registro em uma nova posicao.
    
    public boolean atualizar(int id, long novaPosicao) {

        if (raiz == null) {

            return false;
        }

        return atualizar(raiz, id, novaPosicao);
    }

    // Atualizacao recursiva pra nao perder 

    private boolean atualizar(No no, int id, long novaPosicao) {

        int i = 0;


        while (i < no.quantidade && id > no.ids[i]) {

            i++;
        }

        // Encontrou.

        if (i < no.quantidade && id == no.ids[i]) {

            no.posicoes[i] = novaPosicao;

            return true;
        }


        // Se e folha, nao existe.

        if (no.folha) {

            return false;
        }

        return atualizar(no.filhos[i], id, novaPosicao);
    }


    // Remocao
    // IMPORTANTE CABECAO:
    // O nossa CRUD, o DELETE utiliza lapide no jogos.dat.
    // Portanto, a gemte pode inicialmente NÃO remover da Árvore B.
    // O READ encontra a posição e depois verifica:
    // registro.lapide
    // Se estiver true, retorna null, e basicamente essa a descricao do metodo
   
    public boolean remover(int id) {

        if (raiz == null) {

            return false;
        }

        return remover(raiz, id);
    }


    
    // Remocao simples
    // Esta implementacao remove diretamente de uma folha
    // Para um trabalho que exija remocao completa da arvore B
    // com redistribuicao e fusao de nos, essa parte precisa
    // ser expandida.

    private boolean remover(No no, int id) {

        int i = 0;

        while (i < no.quantidade && id > no.ids[i]) {
            i++;
        }

        // Encontrou.

        if (i < no.quantidade && id == no.ids[i]) {

            // Se for folha,
            // podemos retirar diretamente.

            if (no.folha) {

                for (int j = i; j < no.quantidade - 1; j++) {

                    no.ids[j] = no.ids[j + 1];

                    no.posicoes[j] = no.posicoes[j + 1];
                }


                no.quantidade--;

                return true;
            }

            // Se estiver em no interno,
            // nao removemos diretamente nesta versao.

            return false;
        }

        // Nao encontrou em uma folha.

        if (no.folha) {

            return false;
        }

        return remover(no.filhos[i], id);
    }


   // metodo mostrar 

    public void mostrar() {

        if (raiz == null) {

            System.out.println("Arvore vazia.");

            return;
        }

        mostrar(raiz, 0);
    }


    private void mostrar(No no, int nivel) {


        // Espaçamento para representar
        // os níveis da árvore.

        for (int i = 0; i < nivel; i++) {

            System.out.print("    ");
        }


        System.out.print("[");


        for (int i = 0; i < no.quantidade; i++) {

            System.out.print(no.ids[i]);

            System.out.print(" -> ");

            System.out.print(no.posicoes[i]);


            if (i < no.quantidade - 1) {

                System.out.print(" | ");
            }
        }

        System.out.println("]");

        // Se nao for folha,
        // mostra os filhos.

        if (!no.folha) {

            for (int i = 0;
                 i <= no.quantidade;
                 i++) {

                mostrar(
                        no.filhos[i],
                        nivel + 1
                );
            }
        }
    }
}
