class Celula {

    public int elemento;
    public Celula prox;

    public Celula() {
        this(0);
    }

    public Celula(int elemento) {
        this.elemento = elemento;
        this.prox = null;
    }
}



class Lista {

    private Celula primeiro;
    private Celula ultimo;

    public Lista() {
        primeiro = new Celula();
        ultimo = primeiro;
    }

    // Insere um ID no final da lista.
    public void inserirFim(int elemento) {

        ultimo.prox = new Celula(elemento);
        ultimo = ultimo.prox;
    }

    // Procura um ID dentro da lista.
    public boolean pesquisar(int elemento) {

        Celula i = primeiro.prox;

        while (i != null) {

            if (i.elemento == elemento) {
                return true;
            }

            i = i.prox;
        }

        return false;
    }

    // Retorna o tamanho da lista.
    public int tamanho() {

        int tamanho = 0;

        Celula i = primeiro.prox;

        while (i != null) {

            tamanho++;

            i = i.prox;
        }

        return tamanho;
    }

    // Remove um ID da lista.
    public void removerValor(int elemento) {

        Celula anterior = primeiro;
        Celula atual = primeiro.prox;

        while (atual != null) {

            if (atual.elemento == elemento) {

                anterior.prox = atual.prox;

                if (atual == ultimo) {
                    ultimo = anterior;
                }

                return;
            }

            anterior = atual;
            atual = atual.prox;
        }
    }

    // Mostra todos os IDs da lista.
    public void mostrar() {

        Celula i = primeiro.prox;

        while (i != null) {

            System.out.print(i.elemento + " ");

            i = i.prox;
        }
    }

    // Mostra a interseção entre duas listas.
    public void mostrarIntersecao(Lista outra) {

        Celula i = primeiro.prox;

        while (i != null) {

            if (outra.pesquisar(i.elemento)) {

                System.out.print(i.elemento + " ");
            }

            i = i.prox;
        }
    }

    // Retorna o primeiro elemento da lista.
    public Celula getPrimeiro() {
        return primeiro.prox;
    }
}




class CelulaInvertida {

    public String palavra;
    public Lista ids;
    public CelulaInvertida prox;

    public CelulaInvertida(String palavra) {

        this.palavra = palavra;
        this.ids = new Lista();
        this.prox = null;
    }
}




class ListaInvertida {

    private CelulaInvertida primeiro;
    private CelulaInvertida ultimo;

    public ListaInvertida() {

        primeiro = new CelulaInvertida("");
        ultimo = primeiro;
    }

    // Insere uma palavra e o ID correspondente.
    public void inserir(String palavra, int id) {

        CelulaInvertida i = primeiro.prox;

        while (i != null) {

            // Se a palavra já existe.
            if (i.palavra.equals(palavra)) {

                // Evita colocar o mesmo ID duas vezes.
                if (!i.ids.pesquisar(id)) {

                    i.ids.inserirFim(id);
                }

                return;
            }

            i = i.prox;
        }

        // Se a palavra ainda não existe,
        // cria uma nova célula.
        CelulaInvertida nova =
                new CelulaInvertida(palavra);

        nova.ids.inserirFim(id);

        ultimo.prox = nova;
        ultimo = nova;
    }

    // Pesquisa uma palavra na lista invertida.
    // Retorna os IDs relacionados a ela.
    public Lista pesquisar(String palavra) {

        CelulaInvertida i = primeiro.prox;

        while (i != null) {

            if (i.palavra.equals(palavra)) {

                return i.ids;
            }

            i = i.prox;
        }

        // Se não encontrar, retorna lista vazia.
        return new Lista();
    }

    // Remove um ID associado a uma palavra.
    public void remover(String palavra, int id) {

        CelulaInvertida anterior = primeiro;
        CelulaInvertida atual = primeiro.prox;

        while (atual != null) {

            if (atual.palavra.equals(palavra)) {

                atual.ids.removerValor(id);

                // Se não sobrou nenhum ID para a palavra,
                // remove também a palavra da lista invertida.
                if (atual.ids.tamanho() == 0) {

                    anterior.prox = atual.prox;

                    if (atual == ultimo) {
                        ultimo = anterior;
                    }
                }

                return;
            }

            anterior = atual;
            atual = atual.prox;
        }
    }

    // Mostra toda a lista invertida.
    public void mostrar() {

        CelulaInvertida i = primeiro.prox;

        while (i != null) {

            System.out.print(i.palavra + " -> ");

            i.ids.mostrar();

            System.out.println();

            i = i.prox;
        }
    }

    // ========================================================
    // SALVAR A LISTA EM ARQUIVO
    // ========================================================

    public void salvarArquivo(String nomeArquivo)
            throws IOException {

        DataOutputStream arquivo =
                new DataOutputStream(
                        new FileOutputStream(nomeArquivo));

        // Conta quantas palavras existem.
        int quantidadePalavras = 0;

        CelulaInvertida i = primeiro.prox;

        while (i != null) {

            quantidadePalavras++;

            i = i.prox;
        }

        // Grava a quantidade de palavras.
        arquivo.writeInt(quantidadePalavras);

        i = primeiro.prox;

        while (i != null) {

            // Converte a palavra para bytes.
            byte[] palavraBytes =
                    i.palavra.getBytes("UTF-8");

            // Grava o tamanho da palavra.
            arquivo.writeInt(palavraBytes.length);

            // Grava a palavra.
            arquivo.write(palavraBytes);

            // Grava a quantidade de IDs.
            arquivo.writeInt(i.ids.tamanho());

            // Grava os IDs.
            Celula j = i.ids.getPrimeiro();

            while (j != null) {

                arquivo.writeInt(j.elemento);

                j = j.prox;
            }

            i = i.prox;
        }

        arquivo.close();
    }


    public void carregarArquivo(String nomeArquivo)
            throws IOException {

        DataInputStream arquivo =
                new DataInputStream(
                        new FileInputStream(nomeArquivo));

        // Limpa a lista atual.
        primeiro.prox = null;
        ultimo = primeiro;

        // Lê a quantidade de palavras.
        int quantidadePalavras =
                arquivo.readInt();

        for (int i = 0; i < quantidadePalavras; i++) {

            // Lê o tamanho da palavra.
            int tamanhoPalavra =
                    arquivo.readInt();

            // Cria o vetor de bytes.
            byte[] palavraBytes =
                    new byte[tamanhoPalavra];

            // Lê os bytes.
            arquivo.readFully(palavraBytes);

            // Converte os bytes para String.
            String palavra =
                    new String(palavraBytes, "UTF-8");

            // Lê a quantidade de IDs.
            int quantidadeIds =
                    arquivo.readInt();

            // Cria a célula da palavra.
            CelulaInvertida nova =
                    new CelulaInvertida(palavra);

            // Lê todos os IDs daquela palavra.
            for (int j = 0; j < quantidadeIds; j++) {

                int id = arquivo.readInt();

                nova.ids.inserirFim(id);
            }

            // Coloca a palavra na lista.
            ultimo.prox = nova;
            ultimo = nova;
        }

        arquivo.close();
    }
}



class DuasListasInvertidas {

    private ListaInvertida listaHome;
    private ListaInvertida listaAway;

    public DuasListasInvertidas() {

        listaHome = new ListaInvertida();
        listaAway = new ListaInvertida();
    }

    // ========================================================
    // INSERIR NAS DUAS LISTAS
    // ========================================================

    public void inserir(String homeTeam,
                        String awayTeam,
                        int id) {

        // Primeira lista: homeTeam
        listaHome.inserir(homeTeam, id);

        // Segunda lista: awayTeam
        listaAway.inserir(awayTeam, id);
    }


    public void remover(String homeTeam,
                        String awayTeam,
                        int id) {

        // Remove da lista de homeTeam.
        listaHome.remover(homeTeam, id);

        // Remove da lista de awayTeam.
        listaAway.remover(awayTeam, id);
    }

    // Pesquisa somente na lista de homeTeam.
    public Lista pesquisarHome(String homeTeam) {

        return listaHome.pesquisar(homeTeam);
    }

    // Pesquisa somente na lista de awayTeam.
    public Lista pesquisarAway(String awayTeam) {

        return listaAway.pesquisar(awayTeam);
    }

  

    public void pesquisarDuasListas(String homeTeam,
                                    String awayTeam) {

        Lista idsHome =
                listaHome.pesquisar(homeTeam);

        Lista idsAway =
                listaAway.pesquisar(awayTeam);

        System.out.print("IDs encontrados: ");

        idsHome.mostrarIntersecao(idsAway);

        System.out.println();
    }

    // Mostra a lista de homeTeam.
    public void mostrarHome() {

        System.out.println("LISTA HOME:");

        listaHome.mostrar();
    }

    // Mostra a lista de awayTeam.
    public void mostrarAway() {

        System.out.println("LISTA AWAY:");

        listaAway.mostrar();
    }

 

    public void salvarArquivos()
            throws IOException {

        listaHome.salvarArquivo("listaHome.dat");

        listaAway.salvarArquivo("listaAway.dat");
    }

    

    public void carregarArquivos()
            throws IOException {

        listaHome.carregarArquivo("listaHome.dat");

        listaAway.carregarArquivo("listaAway.dat");
    }
}



