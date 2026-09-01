
import java.io.RandomAccessFile;
import java.io.IOException;

public class CRUD {

    // Este método recebe um Registro e grava ele no arquivo jogos.dat, Atenção com o esse arquivo cabeção
    // esse arquivo pode ser criado autmaticamente caso ele nao exista tipo o do kutsova sla

    public static void CRIATE(Registro registro) throws IOException {

        // Abre o arquivo jogos.dat.
        // o r e de red leitura se voce souber ingles, e o w e de leitura por isso botei ai
        // se ele nao existir vai criar o arquivo
        RandomAccessFile arquivo = new RandomAccessFile("jogos.dat", "rw");

        // essa funcao de baixo leva ele pro final do arquivo sempre inserindo no final,
        // assim evitando que a gente perca as informacoes
        arquivo.seek(arquivo.length());

        // Quando criamos um registro, ele começa como ativo.
        // false significa que o registro NÃO está apagado.
        registro.lapide = false;

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
            Registro registro = new Registro(0, "", "", "", "", "", 0, 0, "");

            // ai aqui a gente converte os bytes pra um novo tipo de registro
            registro.fromByteArray(dados);

            // Verifiquei se o ID do registro é o ID e oque a gente procura
            // Também verifico se o registro não está apagado pela lápide.
            if (registro.id == id && !registro.lapide) {

                // se a gente encontrar e melhor fexar o arquivo antes de retornar
                arquivo.close();
                return registro;
            }
        }

        // o arquivo nao tem o id que a gente queria
        // ou o registro está marcado com lápide
        arquivo.close();

        return null;
    }


    // o update ele encontra o ID e substitui por um outro
    public static boolean UPDATE(int id, Registro novoRegistro)
            throws IOException {

        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "rw");

        // Variável para saber se o registro foi encontrado.
        boolean encontrado = false;

        // as coisas sao a mesma do Read
        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição onde começa o tamanho do registro.
            long posicao = arquivo.getFilePointer();

            int tamanho = arquivo.readInt();

            byte[] dados = new byte[tamanho];

            arquivo.readFully(dados);

            Registro registro =
                    new Registro(0, "", "", "", "", "", 0, 0, "");

            registro.fromByteArray(dados);

            // Verifica se encontramos o ID
            // e se o registro ainda está ativo.
            if (registro.id == id && !registro.lapide) {

                // Mantém o mesmo ID do registro original.
                novoRegistro.id = id;

                // O novo registro também deve estar ativo.
                novoRegistro.lapide = false;

                byte[] novosDados = novoRegistro.toByteArray();

                // Voltamos para o começo do registro.
                arquivo.seek(posicao);

                // Grava o novo tamanho.
                arquivo.writeInt(novosDados.length);

                // Grava o novo registro.
                arquivo.write(novosDados);

                // Indica que foi encontrado e atualiza.
                encontrado = true;

                // Como já encontramos o registro,
                // podemos parar a procura.
                break;
            }
        }

        arquivo.close();

        return encontrado;
    }


    // DELETE
    // procura um registro pelo ID e remove ele do arquivo.
    // agora usamos uma LÁPIDE, então o registro não é realmente removido.
    // ele continua no arquivo, mas fica marcado como apagado.

    public static boolean excluir(int id) throws IOException {

        // Abre o arquivo no modo "rw", pois vamos ler e também modificar.
        RandomAccessFile arquivo =
                new RandomAccessFile("jogos.dat", "rw");

        // Indica se encontrou o registro.
        boolean encontrado = false;

        // mesma coisa dos anteriores a checagem de tamanho
        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição onde começa o tamanho do registro.
            long posicao = arquivo.getFilePointer();

            int tamanho = arquivo.readInt();

            byte[] dados = new byte[tamanho];

            arquivo.readFully(dados);

            Registro registro =
                    new Registro(0, "", "", "", "", "", 0, 0, "");

            registro.fromByteArray(dados);

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

        arquivo.close();

        return encontrado;
    }
}
