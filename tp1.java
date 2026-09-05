import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.RandomAccessFile;

public class tp1Com {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

        String caminhoBin = "jogos.dat";

        try {

            int opcao = -1;

            while (opcao != 0) {

                System.out.println("\n========== MENU ==========");
                System.out.println("1 - Carga da base de dados");
                System.out.println("2 - Criar registro");
                System.out.println("3 - Ler registro");
                System.out.println("4 - Atualizar registro");
                System.out.println("5 - Deletar registro");
                System.out.println("6 - Ordenação externa");
                System.out.println("0 - Sair");
                System.out.println("==========================");

                System.out.print("Escolha uma opção: ");
                opcao = sc.nextInt();
                sc.nextLine();


                // Cargaa

                if (opcao == 1) {

                    String caminhoCsv = "C:\\Users\\Marina\\Documents\\Aeds III\\England 2 CSV-selected-columns.csv";

                    CargaCsv.carregar(caminhoCsv, caminhoBin);

                    System.out.println("Carga realizada com sucesso!");
                }


                //Create

                else if (opcao == 2) {

                    System.out.print("Time da casa: ");
                    String homeTeam = sc.nextLine();

                    System.out.print("Time visitante: ");
                    String awayTeam = sc.nextLine();

                    System.out.print("Arbitro: ");
                    String referee = sc.nextLine();

                    System.out.print("Data: ");
                    String data = sc.nextLine();

                    System.out.print("Gols da casa: ");
                    int golsCasa = sc.nextInt();

                    System.out.print("Gols fora: ");
                    int golsFora = sc.nextInt();

                    sc.nextLine();

                    System.out.print("Resultado: ");
                    String resultado = sc.nextLine();

                    String listaTimes = homeTeam + "|" + awayTeam;

                    Registro registro = new Registro(
                        0,
                        homeTeam,
                        awayTeam,
                        referee,
                        data,
                        listaTimes,
                        golsCasa,
                        golsFora,
                        resultado
                    );

                    CRUD crud = new CRUD(caminhoBin);

                    int id = crud.create(registro);

                    crud.close();

                    System.out.println("Registro criado!");
                    System.out.println("ID: " + id);
                }


                // leia

                else if (opcao == 3) {

                    System.out.print("Digite o ID: ");
                    int id = sc.nextInt();

                    CRUD crud = new CRUD(caminhoBin);

                    Registro registro = crud.read(id);

                    crud.close();

                    if (registro != null) {
                        System.out.println(registro.ToString());
                    }
                    else {
                        System.out.println("Registro não encontrado.");
                    }
                }


                // Update

                else if (opcao == 4) {

                    System.out.print("Digite o ID: ");
                    int id = sc.nextInt();

                    sc.nextLine();

                    System.out.print("Novo time da casa: ");
                    String homeTeam = sc.nextLine();

                    System.out.print("Novo time visitante: ");
                    String awayTeam = sc.nextLine();

                    System.out.print("Novo arbitro: ");
                    String referee = sc.nextLine();

                    System.out.print("Nova data: ");
                    String data = sc.nextLine();

                    System.out.print("Novos gols da casa: ");
                    int golsCasa = sc.nextInt();

                    System.out.print("Novos gols fora: ");
                    int golsFora = sc.nextInt();

                    sc.nextLine();

                    System.out.print("Novo resultado: ");
                    String resultado = sc.nextLine();

                    String listaTimes = homeTeam + "|" + awayTeam;

                    Registro novoRegistro = new Registro(
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

                    CRUD crud = new CRUD(caminhoBin);

                    boolean sucesso = crud.update(novoRegistro);

                    crud.close();

                    if (sucesso) {
                        System.out.println("Registro atualizado!");
                    }
                    else {
                        System.out.println("Registro não encontrado.");
                    }
                }


                //Delete

                else if (opcao == 5) {

                    System.out.print("Digite o ID: ");
                    int id = sc.nextInt();

                    CRUD crud = new CRUD(caminhoBin);

                    boolean sucesso = crud.delete(id);

                    crud.close();

                    if (sucesso) {
                        System.out.println("Registro deletado!");
                    }
                    else {
                        System.out.println("Registro não encontrado.");
                    }
                }


                // Ordena

                else if (opcao == 6) {

                    System.out.print("Número de caminhos: ");
                    int numCaminhos = sc.nextInt();

                    System.out.print(
                        "Máximo de registros em memória: "
                    );

                    int maxRegistros = sc.nextInt();

                    if (numCaminhos > 0 && maxRegistros > 0) {

                        OrdenTeste.ordenar(
                            caminhoBin,
                            numCaminhos,
                            maxRegistros
                        );

                        System.out.println(
                            "Ordenação realizada com sucesso!"
                        );

                    }
                    else {

                        System.out.println(
                            "Os valores devem ser maiores que zero."
                        );
                    }
                }


                // Sair

                else if (opcao == 0) {

                    System.out.println("Programa encerrado.");
                }


                // nao tem essa opcao

                else {

                    System.out.println("Opção inválida!");
                }
            }

        }
        catch (IOException e) {

            System.out.println(
                "Erro ao acessar o arquivo: " + e.getMessage()
            );
        }

        sc.close();
  }// final do main
}


  class CargaCsv {
  public static void carregar(String caminhoCsv, String caminhoBin)throws IOException{
    BufferedReader b = new BufferedReader(new FileReader(caminhoCsv));
    
    RandomAccessFile arq3 = new RandomAccessFile(caminhoBin, "rw");

    arq3.setLength(0);
    arq3.writeInt(0);
    b.readLine();
    int id = 1;
    String linha;

    while((linha = b.readLine()) != null){
      String[] campos = linha.split(",", -1);
    
      String data = campos[0];
      String homeTeam = campos[1];
      String awayTeam = campos[2];
      
      String referee = campos[6];

      int golsCasa = Integer.parseInt(campos[3]);
      int golsFora = Integer.parseInt(campos[4]);

      String resultado = campos[5];

      String listaTimes = homeTeam + "|" + awayTeam;

      Registro r = new Registro( id, homeTeam, awayTeam, referee, data, listaTimes, golsCasa, golsFora, resultado);
      
      byte[] ba = r.toByteArray();
      arq3.writeByte(0); // registro valido
      arq3.writeInt(ba.length);
      arq3.write(ba);

      id++;
    }

    arq3.seek(0);
    arq3.writeInt(id -1);

    b.close();
    arq3.close();

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


class OrdenTeste {
  static final int Tam_BLOCO = 10;

  public static void ordenarBloco(Registro[] bloco, int qtd){
    for(int i = 1; i < qtd; i++){
      Registro aux = bloco[i];
      int j = i -1;

      while(j >= 0 && bloco[j].id > aux.id){
        bloco[j + 1] = bloco[j];
        j--;

      }
      bloco[j + 1] = aux;
    }
  }

  static void gravarRegistro(RandomAccessFile arq , Registro r )throws IOException{
    byte[] dados = r.toByteArray();
    
    arq.writeByte(0); //aqui não é boolean arrumar, 0 = valido, 1 = excluido
    arq.writeInt(dados.length);
    arq.write(dados);
  }

  public static Registro lerRegistro( RandomAccessFile arq) throws IOException{
    if(arq.getFilePointer() >= arq.length()){
      return null;
    }
    byte lapide = arq.readByte();
    int tamanho = arq.readInt();
    byte[] dados = new byte[tamanho];
    arq.readFully(dados);
    Registro r = new Registro( 0, "", "", "", "", "", 0, 0, "");
    r.fromByteArray(dados);

    if(lapide == 1){
      return null;
    }
    return r;
  }

  public static int criarBloxs(String caminho, int maxRegistros) throws IOException{
    RandomAccessFile entrada = new RandomAccessFile(caminho,"r");
    entrada.seek(4);
    Registro[] bloco = new Registro[maxRegistros];
    int quantidadeBlocos = 0;
    while( entrada.getFilePointer() < entrada.length()){
      int qte = 0;
      while(qte < maxRegistros && entrada.getFilePointer() < entrada.length()){
        Registro r = lerRegistro(entrada);
        if(r!= null){
          bloco[qte] = r;
          qte++;

        }
      }
      if(qte > 0){
        ordenarBloco(bloco, qte);
        String nome = "bloco" + quantidadeBlocos + ".tmp";

        RandomAccessFile temp = new RandomAccessFile(nome, "rw");
        temp.setLength(0);
        for (int i = 0; i < qte; i++) {
          gravarRegistro(temp, bloco[i]);
          
        }
        temp.close();
        quantidadeBlocos++;
      }
    }
    entrada.close();
    return quantidadeBlocos;
  }
  public static void intercalacao(String[] nomes, String arqSaida) throws IOException {

    RandomAccessFile[] arquivos = new RandomAccessFile[nomes.length];
    Registro[] registros = new Registro[nomes.length];

    for(int i = 0; i < nomes.length; i++) {
        arquivos[i] = new RandomAccessFile(nomes[i], "r");
        registros[i] = lerRegistro(arquivos[i]);
    }

    RandomAccessFile arquivoSaida =
        new RandomAccessFile(arqSaida, "rw");

    arquivoSaida.setLength(0);

    while(true) {

        int menor = -1;

        for(int i = 0; i < nomes.length; i++) {

            if(registros[i] != null) {

                if(menor == -1 || registros[i].id < registros[menor].id) {
                    menor = i;
                }
            }
        }

        if(menor == -1) {
            break;
        }

        gravarRegistro(arquivoSaida, registros[menor]);

        registros[menor] = lerRegistro(arquivos[menor]);
    }

    arquivoSaida.close();

    for(int i = 0; i < nomes.length; i++) {
        arquivos[i].close();
    }
  }

  public static void ordenar(String caminho, int numCaminhos, int maxRegistros) throws IOException {

    int qteBlocos = criarBloxs(caminho, maxRegistros);
    int rodada = 0;

    while(qteBlocos > 1) {

        int novosBlocos = 0;

        for(int i = 0; i < qteBlocos; i += numCaminhos) {

            int qteArquivos = Math.min(numCaminhos, qteBlocos - i);

            String[] nomes = new String[qteArquivos];

            for(int j = 0; j < qteArquivos; j++) {

                if(rodada == 0) {
                    nomes[j] = "bloco" + (i + j) + ".tmp";
                } else {
                    nomes[j] = "intercalado" + (rodada - 1)
                             + "_" + (i + j) + ".tmp";
                }
            }

            String saida = "intercalado" + rodada + "_" + novosBlocos + ".tmp";

            intercalacao(nomes, saida);

            novosBlocos++;
        }

        qteBlocos = novosBlocos;
        rodada++;
    }

    String arquivoFinal;

    if(rodada == 0) {
        arquivoFinal = "bloco0.tmp";
    } else {
        arquivoFinal = "intercalado" + (rodada - 1) + "_0.tmp";
    }

    copiarFile(arquivoFinal, caminho);
  }
  public static void copiarFile(String origem, String destino) throws IOException{

    RandomAccessFile arquivoOri = new RandomAccessFile(origem, "r");
    RandomAccessFile arquivoDes = new RandomAccessFile(destino, "rw");

    arquivoDes.seek(0);
    int ultimoId = arquivoDes.readInt();
    arquivoDes.setLength(0);

    



    arquivoDes.writeInt(ultimoId);

    while(arquivoOri.getFilePointer() < arquivoOri.length()){

        Registro r = lerRegistro(arquivoOri);

        if(r != null){
            gravarRegistro(arquivoDes, r);
        }
    }

    arquivoOri.close();
    arquivoDes.close();
  }
  


}









 class CRUD {

    private RandomAccessFile arquivo;

    // Abre o arquivo
    public CRUD(String nomeArquivo) throws IOException {
        arquivo = new RandomAccessFile(nomeArquivo, "rw");

        // Se o arquivo estiver vazio, cria o cabeçalho
        if (arquivo.length() == 0) {
            arquivo.writeInt(0);
        }
    }

    // =========================================================
    // CREATE
    // =========================================================
    public int create(Registro registro) throws IOException {

        // Vai para o começo do arquivo
        arquivo.seek(0);

        // Lê o último ID usado
        int ultimoId = arquivo.readInt();

        // Gera o próximo ID
        int novoId = ultimoId + 1;

        registro.id = novoId;

        // Atualiza o último ID usado no cabeçalho
        arquivo.seek(0);
        arquivo.writeInt(novoId);

        // Vai para o final do arquivo
        arquivo.seek(arquivo.length());

        // Transforma o registro em bytes
        byte[] dados = registro.toByteArray();

        // Lápide
        // false = registro válido
        // true  = registro excluído
        arquivo.writeByte(0);

        // Tamanho do registro
        arquivo.writeInt(dados.length);

        // Dados
        arquivo.write(dados);

        return novoId;
    }


    // =========================================================
    // READ
    // =========================================================
    public Registro read(int id) throws IOException {

        // Começa depois do cabeçalho
        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição onde começa a lápide
            long posicao = arquivo.getFilePointer();

            // Lê a lápide
            byte lapide = arquivo.readByte();

            // Lê o tamanho dos dados
            int tamanho = arquivo.readInt();

            // Lê os dados
            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

            // Se não estiver excluído
            if (lapide == 0) {

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

                registro.fromByteArray(dados);

                // Verifica o ID
                if (registro.id == id) {
                    return registro;
                }
            }
        }

        // Não encontrou
        return null;
    }


    // =========================================================
    // READ ALL
    // =========================================================
    public Registro[] readAll() throws IOException {

        // Quantidade máxima possível
        Registro[] registros = new Registro[1000];

        int qtd = 0;

        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            byte lapide = arquivo.readByte();

            int tamanho = arquivo.readInt();

            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

            if (lapide == 0) {

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

                registro.fromByteArray(dados);

                registros[qtd] = registro;
                qtd++;
            }
        }

        Registro[] resposta = new Registro[qtd];

        for (int i = 0; i < qtd; i++) {
            resposta[i] = registros[i];
        }

        return resposta;
    }


  
    public boolean update(Registro novoRegistro) throws IOException {

        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição da lápide
            long posicaoLapide = arquivo.getFilePointer();

            byte lapide = arquivo.readByte();

            int tamanho = arquivo.readInt();

            // Guarda a posição onde começam os dados
            long posicaoDados = arquivo.getFilePointer();

            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

            // Só procura entre registros válidos
            if (lapide == 0) {

                Registro registroAtual = new Registro(
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

                registroAtual.fromByteArray(dados);

                // Encontrou o registro
                if (registroAtual.id == novoRegistro.id) {

                    byte[] novosDados = novoRegistro.toByteArray();

                   
                    if (novosDados.length == tamanho) {

                        arquivo.seek(posicaoDados);
                        arquivo.write(novosDados);

                        return true;
                    }

                   
                    else if (novosDados.length < tamanho) {

                      arquivo.seek(posicaoLapide);
                      arquivo.writeByte(1);
                      arquivo.seek(arquivo.length());


                      arquivo.writeByte(0);
                      arquivo.writeInt(novosDados.length);
                      arquivo.write(novosDados);


                        return true;
                    }

                    
                    else {

                        // Marca o registro antigo como excluído
                        arquivo.seek(posicaoLapide);
                        arquivo.writeByte(1);

                        // Vai para o final
                        arquivo.seek(arquivo.length());

                        // Escreve o novo registro
                        arquivo.writeByte(0);
                        arquivo.writeInt(novosDados.length);
                        arquivo.write(novosDados);

                        return true;
                    }
                }
            }
        }

        return false;
    }


    public boolean delete(int id) throws IOException {

        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição da lápide
            long posicaoLapide = arquivo.getFilePointer();

            byte lapide = arquivo.readByte();

            int tamanho = arquivo.readInt();

            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

            // Só verifica registros válidos
            if (lapide == 0) {

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

                registro.fromByteArray(dados);

                if (registro.id == id) {

                    // Exclusão lógica
                    arquivo.seek(posicaoLapide);
                    arquivo.writeByte(1);

                    return true;
                }
            }
        }

        return false;
    }


   
    public void close() throws IOException {
        arquivo.close();
    }
}







