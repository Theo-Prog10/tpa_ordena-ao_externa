package entity.cms;

import java.io.*;
import java.util.*;

public class MergeSortExterno {

    private static final int TAMANHO_BLOCO = 10000; // Tamanho máximo de cada bloco na memória

    public static void mergeSortExterno(String arquivoEntrada, String arquivoSaida) throws IOException {
        // Passo 1: Dividir o arquivo em blocos menores e ordená-los
        List<String> arquivosTemporarios = divideEOrdenaBlocos(arquivoEntrada);

        // Passo 2: Mesclar os blocos ordenados
        mesclaBlocos(arquivosTemporarios, arquivoSaida);
    }

    public static void ordenarEmMemoria(Cliente[] clientes) {
        if (clientes != null && clientes.length > 1) {
            Arrays.sort(clientes, Comparator.naturalOrder()); // Usa o método de comparação natural
        }
    }


    private static List<String> divideEOrdenaBlocos(String arquivoEntrada) throws IOException {
        List<String> arquivosTemporarios = new ArrayList<>();
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(arquivoEntrada))) {
            int contadorBloco = 0;

            while (true) {
                // Ler o bloco de TAMANHO_BLOCO elementos
                List<Cliente> bloco = new ArrayList<>();
                for (int i = 0; i < TAMANHO_BLOCO; i++) {
                    try {
                        Cliente cliente = (Cliente) entrada.readObject();
                        bloco.add(cliente);
                    } catch (EOFException e) {
                        break;
                    } catch (ClassNotFoundException e) {
                        throw new IOException("Erro ao desserializar cliente", e);
                    }
                }

                if (bloco.isEmpty()) {
                    break;
                }

                // Ordenar o bloco
                bloco.sort(Comparator.naturalOrder());

                // Gravar o bloco ordenado em um arquivo temporário
                String arquivoTemporario = "temp_" + contadorBloco + ".dat";
                try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(arquivoTemporario))) {
                    for (Cliente cliente : bloco) {
                        saida.writeObject(cliente);
                    }
                }
                arquivosTemporarios.add(arquivoTemporario);
                contadorBloco++;
            }
        }
        return arquivosTemporarios;
    }

    private static void mesclaBlocos(List<String> arquivosTemporarios, String arquivoSaida) throws IOException {
        PriorityQueue<BlocoCliente> filaPrioridade = new PriorityQueue<>();

        // Abrir todos os arquivos temporários para leitura
        List<ObjectInputStream> streams = new ArrayList<>();
        for (String arquivo : arquivosTemporarios) {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(arquivo));
            streams.add(stream);

            // Ler o primeiro cliente de cada arquivo
            try {
                Cliente cliente = (Cliente) stream.readObject();
                filaPrioridade.add(new BlocoCliente(cliente, stream));
            } catch (EOFException | ClassNotFoundException e) {
                // Arquivo vazio ou erro de leitura
                stream.close();
            }
        }

        // Mesclar os blocos ordenados em um arquivo de saída
        try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(arquivoSaida))) {
            while (!filaPrioridade.isEmpty()) {
                BlocoCliente bloco = filaPrioridade.poll();
                Cliente cliente = bloco.getCliente();
                saida.writeObject(cliente);

                // Ler o próximo cliente do mesmo arquivo
                try {
                    Cliente proximoCliente = (Cliente) bloco.getStream().readObject();
                    filaPrioridade.add(new BlocoCliente(proximoCliente, bloco.getStream()));
                } catch (EOFException | ClassNotFoundException e) {
                    // Fim do arquivo ou erro de leitura
                    bloco.getStream().close();
                }
            }
        }

        // Limpar arquivos temporários
        for (String arquivo : arquivosTemporarios) {
            new File(arquivo).delete();
        }
    }

    // Classe auxiliar para associar um cliente ao seu arquivo de origem
    private static class BlocoCliente implements Comparable<BlocoCliente> {
        private final Cliente cliente;
        private final ObjectInputStream stream;

        public BlocoCliente(Cliente cliente, ObjectInputStream stream) {
            this.cliente = cliente;
            this.stream = stream;
        }

        public Cliente getCliente() {
            return cliente;
        }

        public ObjectInputStream getStream() {
            return stream;
        }

        @Override
        public int compareTo(BlocoCliente outro) {
            return cliente.compareTo(outro.getCliente());
        }
    }
}
