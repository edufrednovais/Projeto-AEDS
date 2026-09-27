import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class Registro {

    int id;
    byte lapide;
    String campFix;
    String campVariavel;
    String data;
    String lista;
    float valor;

    // edu vai ver o video do Kutova la ele explica isso
    DecimalFormat df = new DecimalFormat("#,##0.00");


    // Construtor
    Registro(int id, String campFix, String campVariavel,String data, String lista, float valor) {

        this.id = id;

        // Registro começa ativo
        this.lapide = 0;

        this.campFix = campFix;
        this.campVariavel = campVariavel;

        // esse da data vamos ter q rever
        this.data = data;

        this.lista = lista;
        this.valor = valor;
    }


    // Transforma objeto em bytes
    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        DataOutputStream dos =
                new DataOutputStream(baos);

        dos.writeInt(id);

        // Grava a lapide como BYTE
        dos.writeByte(lapide);

        dos.writeUTF(campFix);
        dos.writeUTF(campVariavel);
        dos.writeUTF(data);
        dos.writeUTF(lista);
        dos.writeFloat(valor);

        return baos.toByteArray();
    }


    // Transforma bytes em objetos
    public void fromByteArray(byte[] ba) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);

        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();

        // Le a lapide como BYTE
        lapide = dis.readByte();

        campFix = dis.readUTF();
        campVariavel = dis.readUTF();
        data = dis.readUTF();
        lista = dis.readUTF();
        valor = dis.readFloat();
    }


    // to String
    String ToString() {

        return "\nId: " + id +
                "\nCampFix: " + campFix +
                "\nCampVariavel: " + campVariavel +
                "\nData: " + data +
                "\nLista: " + lista +
                "\nValor: RS" + df.format(valor);
    }
}