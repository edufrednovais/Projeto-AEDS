class OrdenTeste {
  static final int Tam_BLOCO = 10; // provavelmnete não vou usar
 
  //Aqui eu usei o insertion Sort, para ordenar os blocos em memoria pelo id
 

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

  // Bem nesse metodo vamos converter o registro para bytes e gravar a lapide junto com o tamanho do registro
  static void gravarRegistro(RandomAccessFile arq , Registro r )throws IOException{
    byte[] dados = r.toByteArray();
    
    arq.writeByte(0); //aqui não é boolean arrumar, 0 = valido, 1 = excluido
    arq.writeInt(dados.length);
    arq.write(dados);
  }

  // Aqui vamos inverter o oq aconetceu no anterior, vamos  ler o registro  do arquivo em bytes bytes converter ele de volta para obejto, ignorando os deletes 
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

  // divide o aqruivo em blocos menores, ordenando em blocos em menmoria e salva em um arquivo temp.
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

  // esse foi dificil em, juntas o blcoos ordenados, sempre pelo menor id, e cria o arquivo ordenado
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
// essa aqui toma conta da ordenação externa  e fica de olho nas rodadas da intercalao e chama o copiarFile
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

  // FInalmente o Ultimo, ta acabando, copia o ultimo arquivo ordenado de volta para o aqruivo principal mantendo o id
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















