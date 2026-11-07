import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CRUDIndex {

    private static final String ARQUIVO = "jogos.dat";

    // Lê o registro que começa na posição atual do arquivo.
    // Formato de cada registro no arquivo: [int tamanho][bytes do registro]
    private static Registro lerRegistro(RandomAccessFile arq) throws IOException {
        int tamanho = arq.readInt();
        byte[] dados = new byte[tamanho];
        arq.readFully(dados);
        Registro r = new Registro(0, "", "", "", "", 0);
        r.fromByteArray(dados);
        return r;
    }

    // Busca sequencial: percorre o arquivo do início ao fim procurando um registro
    // ATIVO (lápide 0) com o ID informado. Retorna a posição onde ele começa,
    // ou -1 se não encontrar. Registros com lápide 1 são ignorados.
    private static long buscarPosicao(int id) throws IOException {
        File f = new File(ARQUIVO);
        if (!f.exists())
            return -1;

        try (RandomAccessFile arq = new RandomAccessFile(f, "r")) {
            while (arq.getFilePointer() < arq.length()) {
                long pos = arq.getFilePointer(); // início deste registro
                Registro r = lerRegistro(arq);
                if (r.lapide == 0 && r.id == id)
                    return pos;
            }
        }
        return -1;
    }

    // CREATE: grava o novo registro no final do arquivo.
    public static void CRIATE(Registro registro) throws IOException {
        try (RandomAccessFile arq = new RandomAccessFile(ARQUIVO, "rw")) {
            arq.seek(arq.length()); // vai para o final
            registro.lapide = 0; // registro novo começa ativo
            byte[] dados = registro.toByteArray();
            arq.writeInt(dados.length); // tamanho antes dos dados
            arq.write(dados);
        }
    }

    // READ: retorna o registro ativo com o ID, ou null se não existir.
    public static Registro READ(int id) throws IOException {
        long pos = buscarPosicao(id);
        if (pos == -1)
            return null;

        try (RandomAccessFile arq = new RandomAccessFile(ARQUIVO, "r")) {
            arq.seek(pos);
            return lerRegistro(arq);
        }
    }

    // UPDATE: se o novo registro tiver o mesmo tamanho, sobrescreve no lugar.
    // Se o tamanho mudar, marca o antigo com lápide e grava o novo no final.
    public static boolean UPDATE(int id, Registro novoRegistro) throws IOException {
        long pos = buscarPosicao(id);
        if (pos == -1)
            return false;

        try (RandomAccessFile arq = new RandomAccessFile(ARQUIVO, "rw")) {
            arq.seek(pos);
            int tamanhoAntigo = arq.readInt();
            byte[] dadosAntigos = new byte[tamanhoAntigo];
            arq.readFully(dadosAntigos);

            novoRegistro.id = id; // o ID nunca muda
            novoRegistro.lapide = 0;
            byte[] novosDados = novoRegistro.toByteArray();

            if (novosDados.length == tamanhoAntigo) {
                // Mesmo tamanho: sobrescreve na mesma posição
                arq.seek(pos);
                arq.writeInt(novosDados.length);
                arq.write(novosDados);
            } else {
                // Tamanho diferente: apaga o antigo (lápide = 1)...
                Registro antigo = new Registro(0, "", "", "", "", 0);
                antigo.fromByteArray(dadosAntigos);
                antigo.lapide = 1;
                byte[] apagado = antigo.toByteArray();
                arq.seek(pos);
                arq.writeInt(apagado.length);
                arq.write(apagado);

                // ...e grava o novo no final do arquivo
                arq.seek(arq.length());
                arq.writeInt(novosDados.length);
                arq.write(novosDados);
            }
        }
        return true;
    }

    // DELETE: exclusão lógica. Marca a lápide como 1; o registro continua no
    // arquivo.
    public static boolean excluir(int id) throws IOException {
        long pos = buscarPosicao(id);
        if (pos == -1)
            return false;

        try (RandomAccessFile arq = new RandomAccessFile(ARQUIVO, "rw")) {
            arq.seek(pos);
            Registro r = lerRegistro(arq);
            r.lapide = 1;
            byte[] dados = r.toByteArray();
            arq.seek(pos);
            arq.writeInt(dados.length);
            arq.write(dados);
        }
        return true;
    }
}