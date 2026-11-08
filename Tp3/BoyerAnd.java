import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.io.RandomAccessFile;

public class BoyerAnd {
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
                System.out.println("7 - Casamento de padrões (Boyer-Moore x Shift-And)");
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


                // Casamento de padrões

                else if (opcao == 7) {

                    System.out.print("Padrão a buscar (1 a 64 caracteres): ");
                    String padrao = sc.nextLine();

                    System.out.println("1-Lista de times  2-Time da casa  3-Time visitante  4-Árbitro  5-Resultado  6-Data");
                    System.out.print("Campo: ");
                    int campo = sc.nextInt();
                    sc.nextLine();

                    if (padrao.isEmpty() || padrao.length() > 64) {
                        System.out.println("O padrão deve ter de 1 a 64 caracteres.");
                    }
                    else if (campo < 1 || campo > 6) {
                        System.out.println("Campo inválido.");
                    }
                    else {
                        BuscaPadrao.buscar(caminhoBin, padrao, campo);
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
    // UTF-8 para não corromper acentos
    BufferedReader b = new BufferedReader(
        new InputStreamReader(new FileInputStream(caminhoCsv), "UTF-8"));

    RandomAccessFile arq3 = new RandomAccessFile(caminhoBin, "rw");

    arq3.setLength(0);
    arq3.writeInt(0);
    b.readLine();
    int id = 1;
    String linha;

    // tirar a , do csv
    while((linha = b.readLine()) != null){
      String[] campos = linha.split(",", -1); // o -1 é pq no csv tem registro que não tem o arbitro então ele so vai ate o 6, porem o index do campo vai ate 7 então coloquei -1 para ignorar esses casos de 6 campos

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


 class CRUD {

    private RandomAccessFile arquivo;

    // Abre o arquivo
    public CRUD(String nomeArquivo) throws IOException {
        arquivo = new RandomAccessFile(nomeArquivo, "rw");

        // Se o arquivo estiver vazio cria o cabeçalho
        if (arquivo.length() == 0) {
            arquivo.writeInt(0);
        }
    }


    public int create(Registro registro) throws IOException {

        // Vai para o começo do arquivo
        arquivo.seek(0);

        int ultimoId = arquivo.readInt();

        int novoId = ultimoId + 1;

        registro.id = novoId;

        arquivo.seek(0);
        arquivo.writeInt(novoId);

        // Vai para o final do arquivo
        arquivo.seek(arquivo.length());

        // Transforma o registro em bytes
        byte[] dados = registro.toByteArray();

        // Lapide  false = registro válido  true  = registro excluído
        arquivo.writeByte(0);

        arquivo.writeInt(dados.length);

        arquivo.write(dados);

        return novoId;
    }



    public Registro read(int id) throws IOException {

        // Começa depois do cabeçalho
        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            long posicao = arquivo.getFilePointer();

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

                // Verifica o ID
                if (registro.id == id) {
                    return registro;
                }
            }
        }

        // Não encontrou
        return null;
    }



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

            long posicaoLapide = arquivo.getFilePointer();

            byte lapide = arquivo.readByte();

            int tamanho = arquivo.readInt();

            long posicaoDados = arquivo.getFilePointer();

            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

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

                        arquivo.seek(posicaoLapide);
                        arquivo.writeByte(1);

                        arquivo.seek(arquivo.length());

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

            long posicaoLapide = arquivo.getFilePointer();

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

                if (registro.id == id) {

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


// Boyer Moore, escolhi porque o nome é muito bonito, usarei a euristica de caracter ruim
// Tbm escolhi ele por ser o unico que compara da direita para esquerda , 
// e ao falhar ele pula usando a ultima posicao do caracter ruim no padrão

class BoyerMoore {

  private final char[] padrao;
  private final int[] ultima = new int[65536]; // última posição de cada char no padrão
  long comparacoes = 0;

  BoyerMoore(String p) {
    padrao = p.toCharArray();
    Arrays.fill(ultima, -1);
    for (int i = 0; i < padrao.length; i++) {
      ultima[padrao[i]] = i;
    }
  }

  // Retorna quantas vezes o padrão ocorre no texto 

  int buscar(String texto) {
    int n = texto.length();
    int m = padrao.length;
    int s = 0; 
    int ocorrencias = 0;

    while (s <= n - m) {
      int j = m - 1;

      // reduz j enquanto padrão e texto casam neste deslocamento
      while (j >= 0) {
        comparacoes++;
        if (padrao[j] != texto.charAt(s + j)) {
          break;
        }
        j--;
      }

      if (j < 0) {
        ocorrencias++; // padrão ocorre no deslocamento s

        // alinha o próximo caractere do texto com sua última ocorrência no padrão
        // s + m < n evita acessar além do fim do texto
        s += (s + m < n) ? m - ultima[texto.charAt(s + m)] : 1;
      } else {
        // salto pelo caracter ruim
        s += Math.max(1, j - ultima[texto.charAt(s + j)]);
      }
    }
    return ocorrencias;
  }
}


// Confesso eu escolhi esse por ser o mais facil de se representar no papel, facilitando a logica
// Simula um autômato com um long: cada bit é um estado do padrão.
// Coloquei o limite do padrão com até 64 caracteres.

class ShiftAnd {


  // posição m-1 representa o 1º caractere do padrão e o bit 0 o último.
  private final long[] mascara = new long[65536]; // máscara de bits de cada caractere
  private final long bitTopo;   // 2^(m-1)
  private final int m;
  long comparacoes = 0;

  ShiftAnd(String padrao) {
    m = padrao.length();
    if (m == 0 || m > 64) {
      throw new IllegalArgumentException("O padrão deve ter de 1 a 64 caracteres."); 
    }
    bitTopo = 1L << (m - 1);
    // monta a máscara de bits de cada símbolo do padrão
    for (int i = 0; i < m; i++) {
      mascara[padrao.charAt(i)] |= bitTopo >>> i;
    }
  }


  int buscar(String texto) {
    long estado = 0; 
    int ocorrencias = 0;
    for (int i = 0; i < texto.length(); i++) {
      comparacoes++;
      // shift a direita, liga o bit do topo e faz AND com a máscara do caractere
      estado = ((estado >>> 1) | bitTopo) & mascara[texto.charAt(i)];
      if ((estado & 1L) != 0) { // bit 0 ligado = padrão inteiro casou
        ocorrencias++;
      }
    }
    return ocorrencias;
  }
}
// basicamente perccore o arquivo le o um regsitro por vez e aplica os dois algoritimos

class BuscaPadrao {

  // Escolhe o campo do registro onde a busca será feita
  static String campo(Registro r, int c) {
    switch (c) {
      case 1:  return r.listaTimes;
      case 2:  return r.homeTeam;
      case 3:  return r.awayTeam;
      case 4:  return r.referee;
      case 5:  return r.resultado;
      default: return r.data;
    }
  }

  static void buscar(String caminhoBin, String padrao, int campo)
      throws IOException {

    BoyerMoore bm = new BoyerMoore(padrao);
    ShiftAnd sa = new ShiftAnd(padrao);

    long tempoBm = 0, tempoSa = 0;
    int achouBm = 0, achouSa = 0, lidos = 0, exibidos = 0;
    long totalOcBm = 0, totalOcSa = 0;

    try (RandomAccessFile arq = new RandomAccessFile(caminhoBin, "r")) {

      arq.seek(4); 

      while (arq.getFilePointer() < arq.length()) {

        byte lapide = arq.readByte();
        int tamanho = arq.readInt();
        byte[] dados = new byte[tamanho];
        arq.readFully(dados);

        if (lapide != 0) {
          continue; 
        }

        Registro r = new Registro(0, "", "", "", "", "", 0, 0, "");
        r.fromByteArray(dados);
        lidos++;

        String texto = campo(r, campo);

        long t0 = System.nanoTime();
        int ocBm = bm.buscar(texto);
        tempoBm += System.nanoTime() - t0;

        t0 = System.nanoTime();
        int ocSa = sa.buscar(texto);
        tempoSa += System.nanoTime() - t0;

        totalOcBm += ocBm;
        totalOcSa += ocSa;
        if (ocBm > 0) achouBm++;
        if (ocSa > 0) achouSa++;

        if (ocBm > 0 && exibidos < 10) {
          System.out.println(r.ToString());
          exibidos++;
        }
      }
    }

    System.out.println("\n        RESULTADO         ");
    System.out.println("Registros ativos lidos: " + lidos);
    System.out.println("(exibindo no máximo 10 registros encontrados)");
    System.out.println("\nBoyer-Moore -> registros encontrados: " + achouBm
        + " | ocorrências: " + totalOcBm
        + " | comparações: " + bm.comparacoes
        + " | tempo: " + tempoBm / 1_000_000.0 + " ms");
    System.out.println("Shift-And   -> registros encontrados: " + achouSa
        + " | ocorrências: " + totalOcSa
        + " | caracteres processados: " + sa.comparacoes
        + " | tempo: " + tempoSa / 1_000_000.0 + " ms");
  }
}
