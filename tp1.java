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


import tp1.Registro;

public class CargaCsv {
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


}//tp1











}
