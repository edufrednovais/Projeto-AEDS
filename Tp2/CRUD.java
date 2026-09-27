
import java.io.RandomAccessFile;
import java.io.IOException;

public class CRUD {

    public static void CRIATE(Registro registro) throws IOException {

        // Abre o arquivo principal o mesmo nosso.
        RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "rw");

        // Vai para o final do arquivo.
        arquivo.seek(arquivo.length());

        // Guarda a posicao onde o registro vai começar mesma coisa do sequencial.
        long posicao = arquivo.getFilePointer();

        // Registro novo começa ativo.
        registro.lapide = 0;

        // Transforma o registro em bytes.
        byte[] dados = registro.toByteArray();

        // Grava o tamanho do registro.
        arquivo.writeInt(dados.length);

        // Grava os dados.
        arquivo.write(dados);

        // Fecha o arquivo principal.
        arquivo.close();


        // Arthurrrrr aqui comeca as alteracoes.
        // Aqui atualiza o indice

        RandomAccessFile indice = new RandomAccessFile("indice.dat", "rw");

        // Vai para o final do indice.
        indice.seek(indice.length());

        // Grava o ID.
        indice.writeInt(registro.id);

        // Grava a posicao do registro no jogos.dat.
        indice.writeLong(posicao);

        // Fecha o indice.
        indice.close();
    }




    // Buscar a posicao do indice.
    // Recebe um ID e retorna a posição dele no jogos.dat.
    // Se não encontrar, retorna -1.
    // Codicao base irmao
    private static long buscarIndice(int id) throws IOException {

        RandomAccessFile indice = new RandomAccessFile("indice.dat", "r");

        // Cada registro do índice possui:
        // int  = 4 bytes
        // long = 8 bytes
        // Total = 12 bytes, isso ai tem que ter uma atencao maior, essa alteracao eu nao achei nos viceos do kutova, ai vi no youtube
        while (indice.getFilePointer() < indice.length()) {

            // Le o ID do indice.
            int idIndice = indice.readInt();

            // Le a posicao no jogos.dat.
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


    // READ
    // Busca um registro utilizando o indice.
    // Diferente do CRUD sequencial, nao percorremos todos, essa alteracao muda muito pra quando a gente trabalhar com arvore
    // os registros do jogos.dat.

    public static Registro READ(int id) throws IOException {

        // Primeiro procura a posicao no indice.
        long posicao = buscarIndice(id);

        // Se nao encontrou no indice.
        if (posicao == -1) {
            return null;
        }

        // Abre o arquivo principal.
        RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "r");

        // Vai diretamente para a posicao encontrada.
        arquivo.seek(posicao);

        // Le o tamanho do registro.
        int tamanho = arquivo.readInt();

        // Cria o vetor de bytes.
        byte[] dados = new byte[tamanho];

        // Le os dados.
        arquivo.readFully(dados);

        // Cria um Registro vazio.
        Registro registro = new Registro(0, "", "", "", "", 0);

        // Converte os bytes para registro.
        registro.fromByteArray(dados);

        // Verifica se o registro esta ativo.
        if (registro.lapide == 1) {

            arquivo.close();

            return null;
        }

        // Fecha o arquivo.
        arquivo.close();

        return registro;
    }


    // UPDATE

    public static boolean UPDATE(int id, Registro novoRegistro)
            throws IOException {

        // Procura a posicao utilizando o indice.
        long posicao = buscarIndice(id);

        // Se nao encontrou o ID.
        if (posicao == -1) {
            return false;
        }

        // Abre o arquivo principal.
        RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "rw");

        // Vai diretamente para a posicao.
        arquivo.seek(posicao);

        // Le o tamanho do registro.
        int tamanho = arquivo.readInt();

        // Cria vetor para os dados.
        byte[] dados = new byte[tamanho];

        // Le os dados.
        arquivo.readFully(dados);

        // Cria Registro temporario.
        Registro registro = new Registro(0, "", "", "", "", 0);

        // Converte bytes para Registro.
        registro.fromByteArray(dados);

        // Verifica se o registro esta ativo.
        if (registro.lapide == 1) {

            arquivo.close();

            return false;
        }

        // Mantem o mesmo ID.
        novoRegistro.id = id;

        // Registro novo fica ativo.
        novoRegistro.lapide = 0;

        // Converte o novo registro para bytes.
        byte[] novosDados =
                novoRegistro.toByteArray();

        // CASO 1
        // Mesmo tamanho

        if (novosDados.length == tamanho) {

            // Voltamos para o começo do registro.
            arquivo.seek(posicao);

            // Grava o tamanho.
            arquivo.writeInt(novosDados.length);

            // Grava os novos dados.
            arquivo.write(novosDados);

            // A posicao nao mudou.
            // Portanto, o índice continua correto.

            arquivo.close();

            return true;
        }

        // CASO 2
        // Tamanho diferente
        else {

            // Marca o registro antigo como apagado.
            registro.lapide = 1;

            // Converte novamente.
            byte[] registroApagado = registro.toByteArray();

            // Volta para a posicao antiga.
            arquivo.seek(posicao);

            // Grava o tamanho antigo.
            arquivo.writeInt(registroApagado.length);

            // Grava o registro com lapide.
            arquivo.write(registroApagado);


            // NOVO REGISTRO

            // Vai para o final do arquivo.
            arquivo.seek(arquivo.length());

            // Guarda a nova posicao.
            long novaPosicao = arquivo.getFilePointer();

            // Grava tamanho do novo registro.
            arquivo.writeInt(novosDados.length);

            // Grava o novo registro.
            arquivo.write(novosDados);

            // Fecha o arquivo.
            arquivo.close();


            // ATUALIZA O ÍNDICE

            atualizarIndice(id, novaPosicao);

            return true;
        }
    }


    // ATUALIZAR ÍNDICE
    // Usado quando o UPDATE precisa colocar o registro
    // no final do arquivo.
    private static void atualizarIndice(int id,long novaPosicao) throws IOException {

        RandomAccessFile indice = new RandomAccessFile("indice.dat", "rw");

        // Procura o ID dentro do indice.
        while (indice.getFilePointer() < indice.length()) {

            // Guarda a posição da entrada do índice.
            long posicaoIndice =
                    indice.getFilePointer();

            // Lê o ID.
            int idIndice =
                    indice.readInt();

            // Lê a posicao antiga.
            indice.readLong();

            // Encontrou o ID.
            if (idIndice == id) {

                // Volta para a posicao onde fica o long.
                indice.seek(posicaoIndice + 4);

                // Atualiza a posicao.
                indice.writeLong(novaPosicao);

                indice.close();

                return;
            }
        }

        indice.close();
    }


    // DELETE
    // Marca o registro com lapide.
    // O registro continua no jogos.dat,
    // mas nao pode mais ser retornado pelo READ.
    public static boolean excluir(int id) throws IOException {

        // Procura a posicao no indice.
        long posicao = buscarIndice(id);

        // Nao encontrou.
        if (posicao == -1) {
            return false;
        }

        // Abre o arquivo.
        RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "rw");

        // Vai diretamente para o registro.
        arquivo.seek(posicao);

        // Le o tamanho.
        int tamanho = arquivo.readInt();

        // Cria vetor.
        byte[] dados = new byte[tamanho];

        // Le os dados.
        arquivo.readFully(dados);

        // Cria Registro.
        Registro registro =
                new Registro(0, "", "", "", "", 0);

        // Converte bytes.
        registro.fromByteArray(dados);

        // Verifica se ja esta apagado.
        if (registro.lapide == 1) {

            arquivo.close();

            return false;
        }

        // Marca como apagado.
        registro.lapide = 1;

        // Converte novamente.
        byte[] novosDados = registro.toByteArray();

        // Volta para o inicio do registro.
        arquivo.seek(posicao);

        // Grava o tamanho.
        arquivo.writeInt(novosDados.length);

        // Grava o registro com lapide.
        arquivo.write(novosDados);

        // Fecha o arquivo.
        arquivo.close();

        return true;
    }
}
