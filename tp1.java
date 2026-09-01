import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class tp1 {
  




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


}//Registro

  

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;




Class CargaCsv {
  public static void carregar(String caminhoCsv, String caminhoBin)throws IOException{
    BufferedReader b = new BufferedReader(new FileReader(caminhoCsv));
    
    RandomAccessFile arq3 = new RandomAccessFile(caminhoBin, "rw");


    arq3.writeInt(0);
    b.readLine();
    int id = 1;
    String linha;

    while((linha = b.readLine()) != null){
      String[] campos = linha.split(",");
      String data = campos[0];
      String homeTeam = campos[1];
      String awayTeam = campos[2];
      String referee = campos[3];

      int golsCasa = Integer.parseInt(campos[4]);
      int golsFora = Integer.parseInt(campos[5]);

      String resultado = campos[6];

      String listaTimes = homeTeam + "|" + awayTeam;

      tp1.Registro r = new tp1.Registro( id, homeTeam, awayTeam, referee, data, listaTimes, golsCasa, golsFora, resultado);
      
      byte[] ba = r.toByteArray();
      arq3.writeByte(1);
      arq3.writeInt(ba.length);
      arq3.write(ba);

      id++;
    }

    arq3.seek(0);
    arq3.writeInt(id -1);

    b.close();
    arq3.close();

  }
}//Carga do Csv



import java.io.RandomAccessFile;
import java.io.IOException;

class CRUD {
    // Este método recebe um Registro e grava ele no arquivo jogos.dat, Atenção com o esse arquivo cabeção
    // esse arquivo pode ser criado autmaticamente caso ele nao exista tipo o do kutsova sla

    public static void CREATE(Registro registro) throws IOException {

        // Abre o arquivo jogos.dat.
       // o r e de red leitura se voce souber ingles, e o w e de leitura por isso botei ai
        // se ele nao existir vai criar o arquivo
        RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "rw");

       // essa funcao de baixo leva ele pro final do arquivo sempre inserindo no final, assim evitando que a gente perca as informacoes
        arquivo.seek(arquivo.length());

        // usei o metodo que voce criou no registo pra converter em byte
        byte[] dados = registro.toByteArray();

        // Primeiro gravamos o tamanho do registro.
        // Isso permite que posteriormente o programa saiba
        // quantos bytes precisa ler para recuperar esse registro.
        arquivo.writeInt(dados.length);

        // usando a funcao la, gravamos os dados no registro
        arquivo.write(dados);

        // Fecha o arquivo.
        arquivo.close();
    }


    // cara o read ele busca por id, so pra deixar claro
    public static Registro READ(int id) throws IOException {

   
    RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "r");

    // Enquanto ainda existirem registros no arquivo
    // getFilePointer() = posição atual dentro do arquivo
    // length() = tamanho total do arquivo
    // acho que essas sao as principais pra voce entender oque eu fiz nesse caso 
    while (arquivo.getFilePointer() < arquivo.length()) {

        int tamanho = arquivo.readInt();
       // acaba que o tamanho do arquivo a gente cria o vetor 
        byte[] dados = new byte[tamanho];

         arquivo.readFully(dados);

        // Criei um Registro vazio temporariamente.
        // a gente precisa de um objeto vazio pra colocar os dados do vetor la
        
        Registro registro = new Registro(0,"","","","","",0,0,"");

       // ai aqui a gente converte os bytes pra um novo tipo de registro 
        registro.fromByteArray(dados);

        // Verifiquei se o ID do registro é o ID e oque a gente procura 
        
        if (registro.id == id) {

           // se a gente encontrar e melhor fexar o arquivo antes de retornar 
            arquivo.close();
            return registro;
        }
    }

    // o arquivo nao tem o id que a gente queria
    arquivo.close();

    return null;
}
// o update ele encontra o ID e substitui por um outro
public static boolean UPDATE(int id, tp1.Registro novoRegistro)
        throws IOException {

     RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "r");

     RandomAccessFile temporario = new RandomAccessFile("jogos_temp.dat", "rw");

    // Variável para saber se o registro foi encontrado.
    boolean encontrado = false;

   // as coisas sao a mesma do Read
    while (arquivo.getFilePointer() < arquivo.length()) {
        int tamanho = arquivo.readInt();

        byte[] dados = new byte[tamanho];

        arquivo.readFully(dados);

        Registro registro = new Registro(0,"","","","","",0,0,"");

        registro.fromByteArray(dados);

        if (registro.id == id) {

            byte[] novosDados = novoRegistro.toByteArray();

            temporario.writeInt(novosDados.length);

            // Grava o novo registro.
            temporario.write(novosDados);

            // Indica que foi encontrado e atualiza.
            encontrado = true;

        } else {
           // esse else e pra se caso nao encontrarmos o registro que queremos atualizar a gente mantem o original 
            temporario.writeInt(tamanho);
            temporario.write(dados);
        }
    }

    arquivo.close();
    temporario.close();

    // Se encontrou o registro
    if (encontrado) {

        // Cria o objetos File para poder manipular
        // os arquivos no sistema.
        java.io.File original =new java.io.File("jogos.dat");

        java.io.File temporarioFile = new java.io.File("jogos_temp.dat");

        // Apaga o arquivo original.
        original.delete();

        // Renomeia o arquivo temporário para jogos.dat.
        temporarioFile.renameTo(original);
    } else {

        // Se não encontrou o ID, apaga o arquivo temporário porque não precisa dele
       
        new java.io.File("jogos_temp.dat").delete();
    }
    return encontrado;
}

// DELETE
// procura um registro pelo ID e remove ele do arquivo.
// usei arquivo temporario pra ficar melhor(nao sei se pode ou nao usar arquivo temporario)

public static boolean DELETE(int id) throws IOException {


    RandomAccessFile arquivo =new RandomAccessFile("jogos.dat", "r");


    RandomAccessFile temporario = new RandomAccessFile("jogos_temp.dat", "rw");

    // Indica se encontrou o registro.
    boolean encontrado = false;

   // mesma coisa dos anteriores a checagem de tamanho 
    while (arquivo.getFilePointer() < arquivo.length()) {

        int tamanho = arquivo.readInt();

        byte[] dados = new byte[tamanho];

        arquivo.readFully(dados);

        tp1.Registro registro = new tp1.Registro(0, "","","","", "", 0,0,"");

        registro.fromByteArray(dados);
       
        if (registro.id == id) {

            encontrado = true;

        } else {

            // se não for o registro que queremos excluir,
            // copiamos normalmente para o arquivo temporário.
            temporario.writeInt(tamanho);
            temporario.write(dados);
        }
    }

    arquivo.close();
    temporario.close();

    if (encontrado) {

        // cria uma referência para o arquivo original.
        java.io.File original =
                new java.io.File("jogos.dat");

        // cria uma referência para o temporário.
        java.io.File temporarioFile =
                new java.io.File("jogos_temp.dat");

        // apaga o arquivo original.
        original.delete();

        // renomeia o temporário para jogos.dat.
        temporarioFile.renameTo(original);

    } else {

        new java.io.File("jogos_temp.dat").delete();
    }

    return encontrado;
}
  
}// Crud

}//tp1











}
