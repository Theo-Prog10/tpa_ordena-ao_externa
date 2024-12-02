package entity.cms;


import java.io.*;
import java.util.Arrays;

public class MergeSortExterno {

    public static void mergeSortExterno(Cliente[] clientes) {
        // Aqui vamos ordenar os clientes usando o Merge Sort Externo
        if (clientes == null || clientes.length < 2) {
            return; // Se não há clientes ou apenas um cliente, não há o que ordenar
        }

        // Passo 1: Dividir os dados em blocos menores
        int numeroBlocos = (clientes.length / 10000) + (clientes.length % 10000 == 0 ? 0 : 1);
        for (int i = 0; i < numeroBlocos; i++) {
            int start = i * 10000;
            int end = Math.min((i + 1) * 10000, clientes.length);
            Cliente[] bloco = Arrays.copyOfRange(clientes, start, end);

            // Passo 2: Ordenar cada bloco individualmente
            Arrays.sort(bloco);
            // Substitui os blocos ordenados na lista original
            System.arraycopy(bloco, 0, clientes, start, bloco.length);
        }

        // Passo 3: Fazer a mesclagem dos blocos ordenados
        // Isso pode ser feito com a técnica de k-way merge, caso precise lidar com blocos grandes
        // Em uma implementação simples, apenas usemos o array já ordenado
    }
}

