/*
 * Diego Tamiozzo
 * Processamento de dados e geração de um relatório em formato de matriz.
 * 20/08/2023
*/
package Performance_motor;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GeraRelatorioMatriz {
    public static void main(String[] args) {
        Map<String, List<String>> dadosAcionadosPorDia = new LinkedHashMap<>();
        Map<String, List<String>> dadosDesligadosPorDia = new LinkedHashMap<>();

        lerDadosDoArquivo("C:\\Users\\user pc\\Desktop\\dados\\dados01.txt", "Entrada 1 Acionada", dadosAcionadosPorDia);
        lerDadosDoArquivo("C:\\Users\\user pc\\Desktop\\dados\\dados02.txt", "Entrada 1 Desligada", dadosDesligadosPorDia);

        calcularDiferencasEGravarMatriz(dadosAcionadosPorDia, dadosDesligadosPorDia);
    }

    private static void lerDadosDoArquivo(String filePath, String targetString, Map<String, List<String>> dadosPorDia) {
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();

                if (linha.contains(targetString)) {
                    String[] dados = extrairDados(linha);
                    if (dados != null) {
                        String dia = dados[0];
                        dadosPorDia.computeIfAbsent(dia, k -> new ArrayList<>()).add(dados[1]);
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
        if (dados.length >= 4) {
            String[] dadosFormatados = new String[2];
            dadosFormatados[0] = dados[1].trim();
            dadosFormatados[1] = dados[3].trim();
            return dadosFormatados;
        }
        return null;
    }

    private static int diferencaHorarios(String hora1, String hora2) {
        String[] horaMinutoSegundo1 = hora1.split(":");
        String[] horaMinutoSegundo2 = hora2.split(":");

        int hora1Int = Integer.parseInt(horaMinutoSegundo1[0]);
        int minuto1Int = Integer.parseInt(horaMinutoSegundo1[1]);
        int segundo1Int = Integer.parseInt(horaMinutoSegundo1[2]);

        int hora2Int = Integer.parseInt(horaMinutoSegundo2[0]);
        int minuto2Int = Integer.parseInt(horaMinutoSegundo2[1]);
        int segundo2Int = Integer.parseInt(horaMinutoSegundo2[2]);

        int totalSegundos1 = hora1Int * 3600 + minuto1Int * 60 + segundo1Int;
        int totalSegundos2 = hora2Int * 3600 + minuto2Int * 60 + segundo2Int;

        return totalSegundos2 - totalSegundos1;
    }

    private static void calcularDiferencasEGravarMatriz(Map<String, List<String>> dadosAcionadosPorDia, Map<String, List<String>> dadosDesligadosPorDia) {
        StringBuilder matrizTexto = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : dadosDesligadosPorDia.entrySet()) {
            String dia = entry.getKey();
            List<String> horariosDesligados = entry.getValue();
            List<String> horariosAcionados = dadosAcionadosPorDia.get(dia);

            if (horariosAcionados != null) {
                matrizTexto.append("Data: ").append(dia).append("\n");
                matrizTexto.append("Nº\tAcionada\tDesligada\tTempo\n");

                for (int i = 0; i < horariosDesligados.size(); i++) {
                    String horarioDesligado = horariosDesligados.get(i);
                    String horarioAcionado = horariosAcionados.get(i);

                    int diff = diferencaHorarios(horarioAcionado, horarioDesligado);
                    matrizTexto.append(i + 1).append("\t").append(horarioAcionado).append("\t").append(horarioDesligado).append("\t").append(diff).append("\n");
                }

                matrizTexto.append("\n");
            }
        }

        gravarMatrizEmArquivo(matrizTexto.toString());
    }

    private static void gravarMatrizEmArquivo(String conteudo) {
        String caminhoDoArquivo = "C:\\Users\\user pc\\Desktop\\dados\\matriz.txt";
        try {
            FileWriter writer = new FileWriter(caminhoDoArquivo);
            writer.write(conteudo);
            writer.close();
            System.out.println("Matriz gravada no arquivo " + caminhoDoArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
