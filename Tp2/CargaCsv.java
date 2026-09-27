
class CargaCsv {

    public static void carregar(String caminhoCsv,
                                String caminhoBin)
            throws IOException {

        BufferedReader b =
                new BufferedReader(
                        new FileReader(caminhoCsv));

        RandomAccessFile arq3 =
                new RandomAccessFile(caminhoBin, "rw");

        // Limpa o arquivo antigo.
        arq3.setLength(0);

        // Cabeçalho com o último ID.
        arq3.writeInt(0);



        RandomAccessFile indice =
                new RandomAccessFile("indice.dat", "rw");

        // Limpa o índice antigo.
        indice.setLength(0);

        // Cria as listas invertidas.
        DuasListasInvertidas listas =
                new DuasListasInvertidas();

        // Pula o cabeçalho do CSV.
        b.readLine();

        int id = 1;
        String linha;

        while ((linha = b.readLine()) != null) {

          String[] campos = linha.split(",", -1); // o -1 é pq no csv tem registro que não tem o arbitro então ele so vai ate o 6, porem o index do campo vai ate 7 então coloquei -1 para ignorar esses casos de 6 campos 
    
            String data = campos[0];
            String homeTeam = campos[1];
            String awayTeam = campos[2];
            
            String referee = campos[6];

            int golsCasa = Integer.parseInt(campos[3]);
            int golsFora = Integer.parseInt(campos[4]);

            String resultado = campos[5];

            String listaTimes = homeTeam + "|" + awayTeam;

            Registro r =
                    new Registro(
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

            // Transforma o Registro em bytes.
            byte[] ba = r.toByteArray();



            long posicao =
                    arq3.getFilePointer();
            // Registro começa ativo.
            arq3.writeByte(0);

            // Grava o tamanho.
            arq3.writeInt(ba.length);

            // Grava os dados.
            arq3.write(ba);

            // Grava o ID.
            indice.writeInt(id);

            // Grava a posição do registro no jogos.dat.
            indice.writeLong(posicao);

            listas.inserir(
                    homeTeam,
                    awayTeam,
                    id
            );

            id++;
        }



        arq3.seek(0);

        arq3.writeInt(id - 1);

        // Fecha os arquivos.
        b.close();
        arq3.close();
        indice.close();


        listas.salvarArquivos();
    }
}


