import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class Registro{
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











}
