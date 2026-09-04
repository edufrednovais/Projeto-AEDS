
import java.io.RandomAccessFile;
import java.io.IOException;

public class CRUD {

    // Este método recebe um Registro e grava ele no arquivo jogos.dat.
    // Atenção com esse arquivo cabeção.
    // Esse arquivo pode ser criado automaticamente caso ele não exista.
    public static void CRIATE(Registro registro) throws IOException {

        // Abre o arquivo jogos.dat.
        // O "r" é de leitura e o "w" é de escrita.
        // Se ele não existir, o Java cria o arquivo.
        RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "rw");

        // Essa função leva ele para o final do arquivo,
        // sempre inserindo no final, assim evitando que
        // a gente perca as informações anteriores.
        arquivo.seek(arquivo.length());

        // Quando criamos um registro, ele começa como ativo.
        // false significa que o registro NÃO está apagado.
        registro.lapide = false;

        // Usei o método que você criou no Registro
        // para converter o objeto em bytes.
        byte[] dados = registro.toByteArray();

        // Primeiro gravamos o tamanho do registro.
        // Isso permite que posteriormente o programa saiba
        // quantos bytes precisa ler para recuperar esse registro.
        arquivo.writeInt(dados.length);

        // Usando a função lá, gravamos os dados do registro.
        arquivo.write(dados);

        // Fecha o arquivo.
        arquivo.close();
    }


    // O READ busca um registro pelo ID.
    public static Registro READ(int id) throws IOException {

        RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "r");

        // Enquanto ainda existirem registros no arquivo.
        // getFilePointer() = posição atual dentro do arquivo.
        // length() = tamanho total do arquivo.
        while (arquivo.getFilePointer() < arquivo.length()) {

            // Lê o tamanho do registro.
            int tamanho = arquivo.readInt();

            // Cria um vetor de bytes com o tamanho
            // que foi armazenado no arquivo.
            byte[] dados = new byte[tamanho];

            // Lê todos os bytes daquele registro.
            arquivo.readFully(dados);

            // Criei um Registro vazio temporariamente.
            // A gente precisa de um objeto vazio para
            // colocar os dados do vetor nele.
            Registro registro =
                    new Registro(0, "", "", "", "", "", 0, 0, "");

            // Aqui a gente converte os bytes para um objeto Registro.
            registro.fromByteArray(dados);

            // Verifico se o ID do registro é o ID que estamos procurando.
            // Também verifico se o registro não está apagado pela lápide.
            if (registro.id == id && !registro.lapide) {

                // Se encontrar, é melhor fechar o arquivo
                // antes de retornar.
                arquivo.close();

                return registro;
            }
        }

        // O arquivo não tem o ID que a gente queria
        // ou o registro está marcado com lápide.
        arquivo.close();

        return null;
    }


    // O UPDATE encontra o ID e atualiza os dados do registro.
    public static boolean UPDATE(int id, Registro novoRegistro)
            throws IOException {

        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "rw");

        // Variável para saber se o registro foi encontrado.
        boolean encontrado = false;

        // Enquanto ainda existirem registros no arquivo.
        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição onde começa o tamanho do registro.
            long posicao = arquivo.getFilePointer();

            // Lê o tamanho do registro atual.
            int tamanho = arquivo.readInt();

            // Cria um vetor para armazenar os dados do registro.
            byte[] dados = new byte[tamanho];

            // Lê os dados do registro.
            arquivo.readFully(dados);

            // Cria um Registro vazio temporariamente.
            Registro registro =
                    new Registro(0, "", "", "", "", "", 0, 0, "");

            // Converte os bytes para um objeto Registro.
            registro.fromByteArray(dados);

            // Verifica se encontramos o ID
            // e se o registro ainda está ativo.
            if (registro.id == id && !registro.lapide) {

                // Mantém o mesmo ID do registro original.
                novoRegistro.id = id;

                // O novo registro também deve estar ativo.
                novoRegistro.lapide = false;

                // Converte o novo registro para bytes.
                byte[] novosDados = novoRegistro.toByteArray();

                if (novosDados.length == tamanho) {

                    // Voltamos para o começo do registro.
                    arquivo.seek(posicao);

                    // Grava o mesmo tamanho.
                    arquivo.writeInt(novosDados.length);

                    // Grava os novos dados no mesmo lugar.
                    arquivo.write(novosDados);

                    // Indica que foi encontrado e atualizado.
                    encontrado = true;
                }


               
                // CASO 2:
                // O novo registro possui tamanho diferente.

                else {

                    // Primeiro precisamos marcar o registro antigo
                    // com uma lápide.
                    registro.lapide = true;

                    // Converte o registro antigo para bytes novamente,
                    // agora com a lápide marcada como true.
                    byte[] registroApagado = registro.toByteArray();

                    // Voltamos para o começo do registro antigo.
                    arquivo.seek(posicao);

                    // Mantemos o tamanho do registro antigo.
                    arquivo.writeInt(registroApagado.length);

                    // Gravamos o registro antigo com a lápide.
                    arquivo.write(registroApagado);


                    // Agora vamos para o final do arquivo.
                    // O novo registro NÃO será colocado no lugar
                    // do registro antigo porque seu tamanho é diferente.
                    arquivo.seek(arquivo.length());

                    // Gravamos o tamanho do novo registro.
                    arquivo.writeInt(novosDados.length);

                    // Gravamos o novo registro no final do arquivo.
                    arquivo.write(novosDados);

                    // Indica que foi encontrado e atualizado.
                    encontrado = true;
                }

                // Como já encontramos o registro,
                // podemos parar a procura.
                break;
            }
        }

        // Fecha o arquivo.
        arquivo.close();

        return encontrado;
    }


    // DELETE
    // Procura um registro pelo ID e remove ele do arquivo.
    // Agora usamos uma LÁPIDE, então o registro não é realmente removido.
    // Ele continua no arquivo, mas fica marcado como apagado.

    public static boolean excluir(int id) throws IOException {

        // Abre o arquivo no modo "rw",
        // pois vamos ler e também modificar.
        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "rw");

        // Indica se encontrou o registro.
        boolean encontrado = false;

        // Enquanto ainda existirem registros no arquivo.
        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição onde começa o tamanho do registro.
            long posicao = arquivo.getFilePointer();

            // Lê o tamanho do registro.
            int tamanho = arquivo.readInt();

            // Cria um vetor para armazenar os dados.
            byte[] dados = new byte[tamanho];

            // Lê todos os bytes do registro.
            arquivo.readFully(dados);

            // Cria um Registro vazio temporariamente.
            Registro registro =
                    new Registro(0, "", "", "", "", "", 0, 0, "");

            // Converte os bytes para um objeto Registro.
            registro.fromByteArray(dados);

            // Verifica se encontramos o ID
            // e se o registro ainda está ativo.
            if (registro.id == id && !registro.lapide) {

                // Colocamos a lápide no registro.
                // true significa que o registro está apagado.
                registro.lapide = true;

                // Transformamos o registro novamente em bytes.
                byte[] novosDados = registro.toByteArray();

                // Voltamos para o início do registro.
                arquivo.seek(posicao);

                // Gravamos novamente o tamanho.
                arquivo.writeInt(novosDados.length);

                // Gravamos o registro com a lápide.
                arquivo.write(novosDados);

                // Indica que encontramos e apagamos o registro.
                encontrado = true;

                // Como já encontramos o registro,
                // podemos parar a procura.
                break;
            }
        }

        // Fecha o arquivo.
        arquivo.close();

        return encontrado;
    }
}

