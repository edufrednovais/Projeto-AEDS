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
      // Arsenal | Chealse

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
}
