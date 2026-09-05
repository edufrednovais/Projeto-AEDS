class OrdenTeste {
  static final int Tam_BLOCO = 10;

  public static void ordenarBloco(Registro[] bloco, int qtd){
    for(int i = 1; i < qtd; i++){
      Registro aux = bloco[i];
      int j = i -1;

      while(j >= 0 && bloco[j].id > aux.id){
        bloco[j + 1] = bloco[j];
        j--;

      }
      bloco[j + 1] = aux;
    }
  }

  static void gravarRegistro(RandomAccessFile arq , Registro r )throws IOException{
    byte[] dados = r.toByteArray();
    
    arq.writeByte(0); //aqui não é boolean arrumar, 0 = valido, 1 = excluido
    arq.writeInt(dados.length);
    arq.write(dados);
  }

  public static Registro lerRegistro( RandomAccessFile arq) throws IOException{
    if(arq.getFilePointer() >= arq.length()){
      return null;
    }
    byte lapide = arq.readByte();
    int tamanho = arq.readInt();
    byte[] dados = new byte[tamanho];
    arq.readFully(dados);
    Registro r = new Registro( 0, "", "", "", "", "", 0, 0, "");
    r.fromByteArray(dados);

    if(lapide == 1){
      return null;
    }
    return r;
  }

  public static int criarBloxs(String caminho, int maxRegistros) throws IOException{
    RandomAccessFile entrada = new RandomAccessFile(caminho,"r");
    entrada.seek(4);
    Registro[] bloco = new Registro[maxRegistros];
    int quantidadeBlocos = 0;
    while( entrada.getFilePointer() < entrada.length()){
      int qte = 0;
      while(qte < maxRegistros && entrada.getFilePointer() < entrada.length()){
        Registro r = lerRegistro(entrada);
        if(r!= null){
          bloco[qte] = r;
          qte++;

        }
      }
      if(qte > 0){
        ordenarBloco(bloco, qte);
        String nome = "bloco" + quantidadeBlocos + ".tmp";

        RandomAccessFile temp = new RandomAccessFile(nome, "rw");
        temp.setLength(0);
        for (int i = 0; i < qte; i++) {
          gravarRegistro(temp, bloco[i]);
          
        }
        temp.close();
        quantidadeBlocos++;
      }
    }
    entrada.close();
    return quantidadeBlocos;
  }
  public static void intercalacao(String[] nomes, String arqSaida) throws IOException {

    RandomAccessFile[] arquivos = new RandomAccessFile[nomes.length];
    Registro[] registros = new Registro[nomes.length];

    for(int i = 0; i < nomes.length; i++) {
        arquivos[i] = new RandomAccessFile(nomes[i], "r");
        registros[i] = lerRegistro(arquivos[i]);
    }

    RandomAccessFile arquivoSaida =
        new RandomAccessFile(arqSaida, "rw");

    arquivoSaida.setLength(0);

    while(true) {

        int menor = -1;

        for(int i = 0; i < nomes.length; i++) {

            if(registros[i] != null) {

                if(menor == -1 || registros[i].id < registros[menor].id) {
                    menor = i;
                }
            }
        }

        if(menor == -1) {
            break;
        }

        gravarRegistro(arquivoSaida, registros[menor]);

        registros[menor] = lerRegistro(arquivos[menor]);
    }

    arquivoSaida.close();

    for(int i = 0; i < nomes.length; i++) {
        arquivos[i].close();
    }
  }

  public static void ordenar(String caminho, int numCaminhos, int maxRegistros) throws IOException {

    int qteBlocos = criarBloxs(caminho, maxRegistros);
    int rodada = 0;

    while(qteBlocos > 1) {

        int novosBlocos = 0;

        for(int i = 0; i < qteBlocos; i += numCaminhos) {

            int qteArquivos = Math.min(numCaminhos, qteBlocos - i);

            String[] nomes = new String[qteArquivos];

            for(int j = 0; j < qteArquivos; j++) {

                if(rodada == 0) {
                    nomes[j] = "bloco" + (i + j) + ".tmp";
                } else {
                    nomes[j] = "intercalado" + (rodada - 1)
                             + "_" + (i + j) + ".tmp";
                }
            }

            String saida = "intercalado" + rodada + "_" + novosBlocos + ".tmp";

            intercalacao(nomes, saida);

            novosBlocos++;
        }

        qteBlocos = novosBlocos;
        rodada++;
    }

    String arquivoFinal;

    if(rodada == 0) {
        arquivoFinal = "bloco0.tmp";
    } else {
        arquivoFinal = "intercalado" + (rodada - 1) + "_0.tmp";
    }

    copiarFile(arquivoFinal, caminho);
  }
  public static void copiarFile(String origem, String destino) throws IOException{

    RandomAccessFile arquivoOri = new RandomAccessFile(origem, "r");
    RandomAccessFile arquivoDes = new RandomAccessFile(destino, "rw");

    arquivoDes.seek(0);
    int ultimoId = arquivoDes.readInt();
    arquivoDes.setLength(0);

    



    arquivoDes.writeInt(ultimoId);

    while(arquivoOri.getFilePointer() < arquivoOri.length()){

        Registro r = lerRegistro(arquivoOri);

        if(r != null){
            gravarRegistro(arquivoDes, r);
        }
    }

    arquivoOri.close();
    arquivoDes.close();
  }
  


}









 class CRUD {

    private RandomAccessFile arquivo;

    // Abre o arquivo
    public CRUD(String nomeArquivo) throws IOException {
        arquivo = new RandomAccessFile(nomeArquivo, "rw");

        // Se o arquivo estiver vazio, cria o cabeçalho
        if (arquivo.length() == 0) {
            arquivo.writeInt(0);
        }
    }

    // =========================================================
    // CREATE
    // =========================================================
    public int create(Registro registro) throws IOException {

        // Vai para o começo do arquivo
        arquivo.seek(0);

        // Lê o último ID usado
        int ultimoId = arquivo.readInt();

        // Gera o próximo ID
        int novoId = ultimoId + 1;

        registro.id = novoId;

        // Atualiza o último ID usado no cabeçalho
        arquivo.seek(0);
        arquivo.writeInt(novoId);

        // Vai para o final do arquivo
        arquivo.seek(arquivo.length());

        // Transforma o registro em bytes
        byte[] dados = registro.toByteArray();

        // Lápide
        // false = registro válido
        // true  = registro excluído
        arquivo.writeByte(0);

        // Tamanho do registro
        arquivo.writeInt(dados.length);

        // Dados
        arquivo.write(dados);

        return novoId;
    }


    // =========================================================
    // READ
    // =========================================================
    public Registro read(int id) throws IOException {

        // Começa depois do cabeçalho
        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição onde começa a lápide
            long posicao = arquivo.getFilePointer();

            // Lê a lápide
            byte lapide = arquivo.readByte();

            // Lê o tamanho dos dados
            int tamanho = arquivo.readInt();

            // Lê os dados
            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

            // Se não estiver excluído
            if (lapide == 0) {

                Registro registro = new Registro(
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    ""
                );

                registro.fromByteArray(dados);

                // Verifica o ID
                if (registro.id == id) {
                    return registro;
                }
            }
        }

        // Não encontrou
        return null;
    }


    // =========================================================
    // READ ALL
    // =========================================================
    public Registro[] readAll() throws IOException {

        // Quantidade máxima possível
        Registro[] registros = new Registro[1000];

        int qtd = 0;

        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            byte lapide = arquivo.readByte();

            int tamanho = arquivo.readInt();

            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

            if (lapide == 0) {

                Registro registro = new Registro(
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    ""
                );

                registro.fromByteArray(dados);

                registros[qtd] = registro;
                qtd++;
            }
        }

        // Cria um vetor exatamente do tamanho necessário
        Registro[] resposta = new Registro[qtd];

        for (int i = 0; i < qtd; i++) {
            resposta[i] = registros[i];
        }

        return resposta;
    }


    // =========================================================
    // UPDATE
    // =========================================================
    public boolean update(Registro novoRegistro) throws IOException {

        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição da lápide
            long posicaoLapide = arquivo.getFilePointer();

            byte lapide = arquivo.readByte();

            int tamanho = arquivo.readInt();

            // Guarda a posição onde começam os dados
            long posicaoDados = arquivo.getFilePointer();

            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

            // Só procura entre registros válidos
            if (lapide == 0) {

                Registro registroAtual = new Registro(
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    ""
                );

                registroAtual.fromByteArray(dados);

                // Encontrou o registro
                if (registroAtual.id == novoRegistro.id) {

                    byte[] novosDados = novoRegistro.toByteArray();

                    // =========================================
                    // CASO 1:
                    // Novo registro tem o mesmo tamanho
                    // =========================================
                    if (novosDados.length == tamanho) {

                        arquivo.seek(posicaoDados);
                        arquivo.write(novosDados);

                        return true;
                    }

                    // =========================================
                    // CASO 2:
                    // Novo registro ficou menor
                    // =========================================
                    else if (novosDados.length < tamanho) {

                      arquivo.seek(posicaoLapide);
                      arquivo.writeByte(1);
                      arquivo.seek(arquivo.length());


                      arquivo.writeByte(0);
                      arquivo.writeInt(novosDados.length);
                      arquivo.write(novosDados);


                        return true;
                    }

                    // =========================================
                    // CASO 3:
                    // Novo registro ficou maior
                    // =========================================
                    else {

                        // Marca o registro antigo como excluído
                        arquivo.seek(posicaoLapide);
                        arquivo.writeByte(1);

                        // Vai para o final
                        arquivo.seek(arquivo.length());

                        // Escreve o novo registro
                        arquivo.writeByte(0);
                        arquivo.writeInt(novosDados.length);
                        arquivo.write(novosDados);

                        return true;
                    }
                }
            }
        }

        return false;
    }


    // =========================================================
    // DELETE
    // =========================================================
    public boolean delete(int id) throws IOException {

        arquivo.seek(4);

        while (arquivo.getFilePointer() < arquivo.length()) {

            // Guarda a posição da lápide
            long posicaoLapide = arquivo.getFilePointer();

            byte lapide = arquivo.readByte();

            int tamanho = arquivo.readInt();

            byte[] dados = new byte[tamanho];
            arquivo.readFully(dados);

            // Só verifica registros válidos
            if (lapide == 0) {

                Registro registro = new Registro(
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    ""
                );

                registro.fromByteArray(dados);

                if (registro.id == id) {

                    // Exclusão lógica
                    arquivo.seek(posicaoLapide);
                    arquivo.writeByte(1);

                    return true;
                }
            }
        }

        return false;
    }


    // =========================================================
    // FECHAR ARQUIVO
    // =========================================================
    public void close() throws IOException {
        arquivo.close();
    }
}








