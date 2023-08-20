/*
 * Diego Tamiozzo
 * Calcular a diferença de tempo entre eventos de "acionamento" e "desligamento" em um motor,
 * separados por datas. Ele lê dados de arquivos, calcula as diferenças de tempo em segundos e
 * imprime os resultados por data.
 * 20/08/2023
*/

package Performance_motor;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Imprimir_tempo {
    public static void main(String[] args) {
        Map<String, List<String[]>> dadosAcionadosPorDia = new HashMap<>();
        Map<String, List<String[]>> dadosDesligadosPorDia = new HashMap<>();

        // Ler dados de "Entrada 1 Acionada" do arquivo "dados01.txt"
        lerDadosDoArquivo("C:\\Users\\user pc\\Desktop\\dados\\dados01.txt", "Entrada 1 Acionada", dadosAcionadosPorDia);

        // Ler dados de "Entrada 1 Desligada" do arquivo "dados02.txt"
        lerDadosDoArquivo("C:\\Users\\user pc\\Desktop\\dados\\dados02.txt", "Entrada 1 Desligada", dadosDesligadosPorDia);

        // Separar os dados por datas em matrizes individuais
        Map<String, List<Long>> diferencaTempoPorDia = calcularDiferencaTempo(dadosAcionadosPorDia, dadosDesligadosPorDia);

        // Imprimir os resultados em ordem crescente das datas
        imprimirResultados(diferencaTempoPorDia);
    }

    private static void lerDadosDoArquivo(String filePath, String targetString, Map<String, List<String[]>> dadosPorDia) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();

                if (linha.contains(targetString)) {
                    String[] dados = extrairDados(linha);
                    if (dados != null) {
                        String dia = dados[0]; // Data
                        dadosPorDia.computeIfAbsent(dia, k -> new ArrayList<>()).add(dados);
                    }
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String[] extrairDados(String linha) {
        String[] dados = linha.split(",");
        if (dados.length >= 2) {
            String[] dadosFormatados = new String[2];
            dadosFormatados[0] = dados[1].trim(); // Data
            dadosFormatados[1] = dados[3].trim(); // Hora
            return dadosFormatados;
        }
        return null;
    }

    private static Map<String, List<Long>> calcularDiferencaTempo(Map<String, List<String[]>> dadosAcionadosPorDia, Map<String, List<String[]>> dadosDesligadosPorDia) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Map<String, List<Long>> diferencaTempoPorDia = new HashMap<>();

        for (String dia : dadosAcionadosPorDia.keySet()) {
            List<String[]> acionadas = dadosAcionadosPorDia.get(dia);
            List<String[]> desligadas = dadosDesligadosPorDia.get(dia);

            if (acionadas != null && desligadas != null) {
                List<Long> temposPorDia = new ArrayList<>();
                for (int i = 0; i < acionadas.size() && i < desligadas.size(); i++) {
                    String[] acionada = acionadas.get(i);
                    String[] desligada = desligadas.get(i);

                    try {
                        Date dataHoraAcionada = dateFormat.parse(acionada[0] + " " + acionada[1]);
                        Date dataHoraDesligada = dateFormat.parse(desligada[0] + " " + desligada[1]);
                        long diferencaEmSegundos = (dataHoraDesligada.getTime() - dataHoraAcionada.getTime()) / 1000;
                        temposPorDia.add(diferencaEmSegundos);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                diferencaTempoPorDia.put(dia, temposPorDia);
            }
        }

        return diferencaTempoPorDia;
    }

    private static void imprimirResultados(Map<String, List<Long>> diferencaTempoPorDia) {
        // Obtém as chaves (datas) e as armazena em uma lista
        List<String> datasOrdenadas = new ArrayList<>(diferencaTempoPorDia.keySet());

        // Ordena a lista de datas em ordem crescente
        Collections.sort(datasOrdenadas);

        // Itera pelas datas ordenadas
        for (String dia : datasOrdenadas) {
            List<Long> temposPorDia = diferencaTempoPorDia.get(dia);
            if (temposPorDia != null) {
                System.out.println(dia + ":");
                int contador = 1;
                for (long tempo : temposPorDia) {
                    System.out.println(contador + " - Tempo de Duração do Evento: " + tempo + " segundos");
                    contador++;
                }
                System.out.println();
            }
        }
    }
}
