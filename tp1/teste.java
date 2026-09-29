package tp1;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

import CRUD;
import CargaCsv;
import Registro;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;


public class teste {
  
    public static void main(String[] args) throws Exception {

         CargaCsv.carregar(
        "C:\\Users\\Marina\\Documents\\Aeds III\\England 2 CSV-selected-columns.csv",
        "jogos.dat"
    );

    DuasListasInvertidas listas = new DuasListasInvertidas();
    listas.carregarArquivos();

    Scanner sc = new Scanner(System.in);

    int opcao;

    do {

        System.out.println("\n========== MENU ==========");
        System.out.println("1 - Buscar time como mandante");
        System.out.println("2 - Buscar time como visitante");
        System.out.println("3 - Buscar usando as duas listas");
        System.out.println("4 - Criar registro");
        System.out.println("5 - Alterar registro");
        System.out.println("6 - Excluir registro");
        System.out.println("0 - Sair");
        System.out.println("==========================");
        System.out.print("Digite uma opção: ");

        opcao = sc.nextInt();
        sc.nextLine();

        switch (opcao) {

            case 1:
                System.out.print("Digite o time mandante: ");
                String home = sc.nextLine();

                Lista resultadoHome = listas.pesquisarHome(home);

                System.out.print("IDs encontrados: ");
                resultadoHome.mostrar();
                System.out.println();
                break;

            case 2:
                System.out.print("Digite o time visitante: ");
                String away = sc.nextLine();

                Lista resultadoAway = listas.pesquisarAway(away);

                System.out.print("IDs encontrados: ");
                resultadoAway.mostrar();
                System.out.println();
                break;
            
            case 3:
                System.out.print("Digite o time mandante: ");
                String homeBusca = sc.nextLine();

                System.out.print("Digite o time visitante: ");
                String awayBusca = sc.nextLine();

                listas.pesquisarDuasListas(homeBusca, awayBusca);
                break;
        
            case 4:

                System.out.println("\n===== CRIAR REGISTRO =====");

                System.out.print("Digite o time mandante: ");
                String Newhome = sc.nextLine();

                System.out.print("Digite o time visitante: ");
                String Newaway = sc.nextLine();

                int novoId = CRUD.proximoId();

                Registro novo = new Registro(
                    novoId,
                    Newhome,
                    Newaway,
                    "",
                    "",
                    Newhome + "|" + Newaway,
                    0,
                    0,
                    ""
                );

                CRUD.CRIATE(novo);

                // Recarrega as listas depois de criar
                listas = new DuasListasInvertidas();
                listas.carregarArquivos();

                System.out.println("Registro criado com sucesso!");
                System.out.println("ID gerado: " + novoId);

                break;

            
            case 5:

                System.out.println("\n===== ALTERAR REGISTRO =====");

                System.out.print("Digite o ID do registro que deseja alterar: ");
                int idAlterar = Integer.parseInt(sc.nextLine());

                Registro atual = CRUD.READ(idAlterar);

                if (atual == null) {
                    System.out.println("Registro não encontrado.");
                    break;
                }

                System.out.println("\nRegistro atual:");
                System.out.println(atual.ToString());

                System.out.print("Novo nome do time mandante: ");
                String novoHome = sc.nextLine();

                System.out.print("Novo nome do time visitante: ");
                String novoAway = sc.nextLine();

                Registro novoRegistro = new Registro(
                    idAlterar,
                    novoHome,
                    novoAway,
                    atual.referee,
                    atual.data,
                    novoHome + "|" + novoAway,
                    atual.golsCasa,
                    atual.golsFora,
                    atual.resultado
                );

                CRUD.UPDATE(idAlterar, novoRegistro);

                // Recarrega as listas depois de alterar
                listas = new DuasListasInvertidas();
                listas.carregarArquivos();

                System.out.println("Registro alterado com sucesso!");

                break;

            
            case 6:

                System.out.println("\n===== EXCLUIR REGISTRO =====");

                System.out.print("Digite o ID do registro que deseja excluir: ");
                int idExcluir = Integer.parseInt(sc.nextLine());

                CRUD.excluir(idExcluir);

                // Recarrega as listas depois de excluir
                listas = new DuasListasInvertidas();
                listas.carregarArquivos();

                System.out.println("Registro excluído com sucesso!");

                break;
            case 0:
                System.out.println("Programa encerrado.");
                break;

            default:
                System.out.println("Opção inválida.");
        }

    } while (opcao != 0);

    sc.close();
    }
}


  class Registro{
  int id;
  
  //campo Fixo
  String homeTeam;
  String awayTeam;


  //campo Variave
  String referee;

  //campo data
  String data;

  //lista com separador
  String listaTimes;

  //inteiro
  int golsCasa;
  int golsFora;

  //campo variavel
  String resultado;

  // edu vai ver o video do Kutova la ele explica isso
  DecimalFormat df = new DecimalFormat("#,##0.00");

  // Construtor
  Registro(int id , String homeTeam, String awayTeam,String referee,String data,String listaTimes , int golsCasa,int golsFora, String resultado){
    this.id = id;
    this.homeTeam = homeTeam;
    this.awayTeam = awayTeam;
    this.referee = referee;
    // esse da data vamos ter q rever
    this.data = data;
    this.listaTimes = listaTimes;
    this.golsCasa = golsCasa;
    this.golsFora = golsFora;
    this.resultado = resultado;
    
  }

  // Transforma objeto em bytes
  public byte[] toByteArray() throws IOException{
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(id);
    dos.writeUTF(homeTeam);
    dos.writeUTF(awayTeam);
    dos.writeUTF(referee);
    dos.writeUTF(data);
    dos.writeUTF(listaTimes);
    dos.writeInt(golsCasa);
    dos.writeInt(golsFora);
    dos.writeUTF(resultado);

    return baos.toByteArray();
  }

  // Transforma bytes em objetos
  public void fromByteArray(byte [] ba) throws IOException{
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    id = dis.readInt();
    homeTeam = dis.readUTF();
    awayTeam = dis.readUTF();
    referee  = dis.readUTF();
    data = dis.readUTF();
    listaTimes = dis.readUTF();
    golsCasa = dis.readInt(); 
    golsFora = dis.readInt();
    resultado = dis.readUTF();
  }
  // to String

  String ToString(){
    return "\nId: " + id + 
      "\nTime da Casa: " + homeTeam +
      "\nTime visitante: " + awayTeam +
      "\nArbitro: " + referee +
      "\nData: " + data +
      "\nLista de Times: " + listaTimes +
      "\nGols casa: " + golsCasa +
      "\nGols fora: " + golsFora +
      "\nResultados: " + resultado;
  }





}






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


// ============================================================
// CELULA DA LISTA INVERTIDA
// ============================================================

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


// ============================================================
// LISTA INVERTIDA
// ============================================================

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

    // ========================================================
    // CARREGAR A LISTA DO ARQUIVO
    // ========================================================

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


// ============================================================
// DUAS LISTAS INVERTIDAS
// ============================================================

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

    // ========================================================
    // REMOVER DAS DUAS LISTAS
    // ========================================================

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

    // ========================================================
    // PESQUISA USANDO AS DUAS LISTAS
    // ========================================================

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

    // ========================================================
    // SALVAR OS DOIS ARQUIVOS
    // ========================================================

    public void salvarArquivos()
            throws IOException {

        listaHome.salvarArquivo("listaHome.dat");

        listaAway.salvarArquivo("listaAway.dat");
    }

    // ========================================================
    // CARREGAR OS DOIS ARQUIVOS
    // ========================================================

    public void carregarArquivos()
            throws IOException {

        listaHome.carregarArquivo("listaHome.dat");

        listaAway.carregarArquivo("listaAway.dat");
    }
}





class CRUD {

    static DuasListasInvertidas listas = new DuasListasInvertidas();
    static boolean listasCarregadas = false;

    // ajudar o main
    public static int proximoId() throws IOException {

        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "rw");

        // Vai para o começo do arquivo,
        // onde está armazenado o último ID.
        arquivo.seek(0);

        // Lê o último ID utilizado.
        int ultimoId = arquivo.readInt();

        // O próximo ID será o último + 1.
        int novoId = ultimoId + 1;

        // Atualiza o cabeçalho com o novo último ID.
        arquivo.seek(0);
        arquivo.writeInt(novoId);

        arquivo.close();

        return novoId;
    }
        
    // CARREGAR LISTAS INVERTIDAS
  

    private static void carregarListas() throws IOException {

        if (!listasCarregadas) {

            File arquivoHome = new File("listaHome.dat");
            File arquivoAway = new File("listaAway.dat");

            if (arquivoHome.exists() && arquivoAway.exists()) {
                listas.carregarArquivos();
            }

            listasCarregadas = true;
        }
    }

    // ============================================================
    // CREATE
    // ============================================================

    public static void CRIATE(Registro registro) throws IOException {

        carregarListas();

        // Abre o arquivo principal.
        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "rw");

        // Vai para o final do arquivo.
        arquivo.seek(arquivo.length());

        // Guarda a posição onde o registro começa.
        long posicao = arquivo.getFilePointer();

        // Registro novo começa ativo.
        byte lapide = 0;

        // Transforma o Registro em bytes.
        byte[] dados = registro.toByteArray();

        // ========================================================
        // GRAVA O REGISTRO
        // [lapide][tamanho][dados]
        // ========================================================

        arquivo.writeByte(lapide);
        arquivo.writeInt(dados.length);
        arquivo.write(dados);

        arquivo.close();

        // ========================================================
        // ATUALIZA O ÍNDICE
        // ========================================================

        RandomAccessFile indice =
                new RandomAccessFile("indice.dat", "rw");

        // Vai para o final do índice.
        indice.seek(indice.length());

        // Grava o ID.
        indice.writeInt(registro.id);

        // Grava a posição do registro no jogos.dat.
        indice.writeLong(posicao);

        indice.close();

        // ========================================================
        // ATUALIZA AS LISTAS INVERTIDAS
        // ========================================================

        listas.inserir(
                registro.homeTeam,
                registro.awayTeam,
                registro.id
        );

        listas.salvarArquivos();
    }

    // ============================================================
    // BUSCAR POSIÇÃO NO ÍNDICE
    // ============================================================

    private static long buscarIndice(int id) throws IOException {

        RandomAccessFile indice =
                new RandomAccessFile("indice.dat", "r");

        // Cada entrada possui:
        // int ID = 4 bytes
        // long posição = 8 bytes
        // Total = 12 bytes

        while (indice.getFilePointer() < indice.length()) {

            // Lê o ID.
            int idIndice = indice.readInt();

            // Lê a posição no jogos.dat.
            long posicao = indice.readLong();

            // Verifica se encontrou o ID.
            if (idIndice == id) {

                indice.close();
                return posicao;
            }
        }

        indice.close();

        return -1;
    }

    // ============================================================
    // READ
    // ============================================================

    public static Registro READ(int id) throws IOException {

        // Procura a posição no índice.
        long posicao = buscarIndice(id);

        // Se não encontrou.
        if (posicao == -1) {
            return null;
        }

        // Abre o arquivo principal.
        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "r");

        // Vai diretamente para a posição.
        arquivo.seek(posicao);

        // ========================================================
        // LÊ A LÁPIDE
        // ========================================================

        byte lapide = arquivo.readByte();

        // Lê o tamanho do registro.
        int tamanho = arquivo.readInt();

        // Cria o vetor de bytes.
        byte[] dados = new byte[tamanho];

        // Lê os dados.
        arquivo.readFully(dados);

        arquivo.close();

        // ========================================================
        // VERIFICA SE ESTÁ APAGADO
        // ========================================================

        if (lapide == 1) {
            return null;
        }

        // Cria um Registro vazio.
        Registro registro = new Registro(
                0,
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                ""
        );

        // Converte os bytes para Registro.
        registro.fromByteArray(dados);

        return registro;
    }

    // ============================================================
    // UPDATE
    // ============================================================

    public static boolean UPDATE(
            int id,
            Registro novoRegistro) throws IOException {

        carregarListas();

        // Procura a posição no índice.
        long posicao = buscarIndice(id);

        // Se não encontrou.
        if (posicao == -1) {
            return false;
        }

        // Abre o arquivo principal.
        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "rw");

        // Vai diretamente para o registro.
        arquivo.seek(posicao);

        // ========================================================
        // LÊ O REGISTRO ANTIGO
        // ========================================================

        byte lapide = arquivo.readByte();

        int tamanho = arquivo.readInt();

        byte[] dados = new byte[tamanho];

        arquivo.readFully(dados);

        // Se já estiver apagado.
        if (lapide == 1) {

            arquivo.close();
            return false;
        }

        // Cria Registro temporário.
        Registro registro = new Registro(
                0,
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                ""
        );

        // Converte os bytes.
        registro.fromByteArray(dados);

        // Guarda os dados antigos.
        String homeAntigo = registro.homeTeam;
        String awayAntigo = registro.awayTeam;

        // Mantém o mesmo ID.
        novoRegistro.id = id;

        // Converte o novo registro para bytes.
        byte[] novosDados =
                novoRegistro.toByteArray();

        // ========================================================
        // CASO 1 - MESMO TAMANHO
        // ========================================================

        if (novosDados.length == tamanho) {

            // Volta para o começo do registro.
            arquivo.seek(posicao);

            // Registro continua ativo.
            arquivo.writeByte(0);

            // Grava o tamanho.
            arquivo.writeInt(novosDados.length);

            // Grava os dados.
            arquivo.write(novosDados);

            arquivo.close();

            // Remove os dados antigos das listas.
            listas.remover(
                    homeAntigo,
                    awayAntigo,
                    id
            );

            // Adiciona os dados novos.
            listas.inserir(
                    novoRegistro.homeTeam,
                    novoRegistro.awayTeam,
                    id
            );

            // Salva as listas.
            listas.salvarArquivos();

            // A posição não mudou.
            return true;
        }

        // ========================================================
        // CASO 2 - TAMANHO DIFERENTE
        // ========================================================

        else {

            // ====================================================
            // MARCA O REGISTRO ANTIGO COMO APAGADO
            // ====================================================

            arquivo.seek(posicao);

            // Lápide 1 = apagado.
            arquivo.writeByte(1);

            // Mantém o tamanho antigo.
            arquivo.writeInt(tamanho);

            // Mantém os dados antigos.
            arquivo.write(dados);

            // ====================================================
            // GRAVA O NOVO REGISTRO NO FINAL
            // ====================================================

            arquivo.seek(arquivo.length());

            // Guarda a nova posição.
            long novaPosicao =
                    arquivo.getFilePointer();

            // Novo registro começa ativo.
            arquivo.writeByte(0);

            // Grava o tamanho.
            arquivo.writeInt(novosDados.length);

            // Grava os dados.
            arquivo.write(novosDados);

            arquivo.close();

            // ====================================================
            // ATUALIZA O ÍNDICE
            // ====================================================

            atualizarIndice(id, novaPosicao);

            // ====================================================
            // ATUALIZA AS LISTAS INVERTIDAS
            // ====================================================

            // Remove os dados antigos.
            listas.remover(
                    homeAntigo,
                    awayAntigo,
                    id
            );

            // Adiciona os dados novos.
            listas.inserir(
                    novoRegistro.homeTeam,
                    novoRegistro.awayTeam,
                    id
            );

            // Salva as listas.
            listas.salvarArquivos();

            return true;
        }
    }

    // ============================================================
    // ATUALIZAR ÍNDICE
    // ============================================================

    private static void atualizarIndice(
            int id,
            long novaPosicao) throws IOException {

        RandomAccessFile indice =
                new RandomAccessFile("indice.dat", "rw");

        while (indice.getFilePointer() < indice.length()) {

            // Guarda a posição da entrada.
            long posicaoIndice =
                    indice.getFilePointer();

            // Lê o ID.
            int idIndice =
                    indice.readInt();

            // Lê a posição antiga.
            indice.readLong();

            // Encontrou o ID.
            if (idIndice == id) {

                // Volta para onde está o long.
                indice.seek(posicaoIndice + 4);

                // Atualiza a posição.
                indice.writeLong(novaPosicao);

                indice.close();
                return;
            }
        }

        indice.close();
    }

    // ============================================================
    // DELETE
    // ============================================================

    public static boolean excluir(int id) throws IOException {

        carregarListas();

        // Procura a posição no índice.
        long posicao = buscarIndice(id);

        // Não encontrou.
        if (posicao == -1) {
            return false;
        }

        // Abre o arquivo.
        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "rw");

        // Vai até o registro.
        arquivo.seek(posicao);

        // ========================================================
        // LÊ O REGISTRO
        // ========================================================

        byte lapide = arquivo.readByte();

        int tamanho = arquivo.readInt();

        byte[] dados = new byte[tamanho];

        arquivo.readFully(dados);

        // Se já estiver apagado.
        if (lapide == 1) {

            arquivo.close();
            return false;
        }

        // Cria Registro.
        Registro registro = new Registro(
                0,
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                ""
        );

        // Converte os dados.
        registro.fromByteArray(dados);

        // Guarda os dados para remover das listas.
        String homeAntigo = registro.homeTeam;
        String awayAntigo = registro.awayTeam;

        // ========================================================
        // MARCA COMO APAGADO
        // ========================================================

        arquivo.seek(posicao);

        // Lápide 1 = apagado.
        arquivo.writeByte(1);

        // Mantém o tamanho.
        arquivo.writeInt(tamanho);

        // Mantém os dados.
        arquivo.write(dados);

        arquivo.close();

        // ========================================================
        // REMOVE DAS LISTAS INVERTIDAS
        // ========================================================

        listas.remover(
                homeAntigo,
                awayAntigo,
                id
        );

        // Salva as listas.
        listas.salvarArquivos();

        return true;
    }
}


class CargaCsv {

    public static void carregar(String caminhoCsv,
                                String caminhoBin)
            throws IOException {

        BufferedReader b =
                new BufferedReader(
                        new FileReader(caminhoCsv));

        RandomAccessFile arq3 =
                new RandomAccessFile(caminhoBin, "rw");

        // Limpa o arquivo antigo.
        arq3.setLength(0);

        // Cabeçalho com o último ID.
        arq3.writeInt(0);



        RandomAccessFile indice =
                new RandomAccessFile("indice.dat", "rw");

        // Limpa o índice antigo.
        indice.setLength(0);

        // Cria as listas invertidas.
        DuasListasInvertidas listas =
                new DuasListasInvertidas();

        // Pula o cabeçalho do CSV.
        b.readLine();

        int id = 1;
        String linha;

        while ((linha = b.readLine()) != null) {

          String[] campos = linha.split(",", -1); // o -1 é pq no csv tem registro que não tem o arbitro então ele so vai ate o 6, porem o index do campo vai ate 7 então coloquei -1 para ignorar esses casos de 6 campos 
    
            String data = campos[0];
            String homeTeam = campos[1];
            String awayTeam = campos[2];
            
            String referee = campos[6];

            int golsCasa = Integer.parseInt(campos[3]);
            int golsFora = Integer.parseInt(campos[4]);

            String resultado = campos[5];

            String listaTimes = homeTeam + "|" + awayTeam;

            Registro r =
                    new Registro(
                            id,
                            homeTeam,
                            awayTeam,
                            referee,
                            data,
                            listaTimes,
                            golsCasa,
                            golsFora,
                            resultado
                    );

            // Transforma o Registro em bytes.
            byte[] ba = r.toByteArray();



            long posicao =
                    arq3.getFilePointer();
            // Registro começa ativo.
            arq3.writeByte(0);

            // Grava o tamanho.
            arq3.writeInt(ba.length);

            // Grava os dados.
            arq3.write(ba);

            // Grava o ID.
            indice.writeInt(id);

            // Grava a posição do registro no jogos.dat.
            indice.writeLong(posicao);

            listas.inserir(
                    homeTeam,
                    awayTeam,
                    id
            );

            id++;
        }



        arq3.seek(0);

        arq3.writeInt(id - 1);

        // Fecha os arquivos.
        b.close();
        arq3.close();
        indice.close();


        listas.salvarArquivos();
    }
}


