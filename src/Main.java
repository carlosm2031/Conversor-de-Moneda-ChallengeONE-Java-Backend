import com.google.gson.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;


public class Main {
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .create();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean loop = true;
            while (loop) {
                mostrarMenu();
                String opcion = scanner.nextLine();
                switch (opcion) {
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                        realizarConversion(opcion, scanner);
                    case "7":
                        loop = false;
                        break;
                    default:
                        System.out.println("Opción no válida. Inténtalo de nuevo.");
                }
            }
        } catch (Exception e) {
            System.out.println("Se produjo un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("""
                *********************************
                Sea bienvenido al Conversor de Moneda
                1) Dólar =>> Peso argentino
                2) Peso argentino =>> dolar
                3) Dólar =>> Real brasileño
                4) Real brasileño =>> Dólar
                5) Dólar =>> Peso colombiano
                6) Peso colombiano =>> Dólar
                7) Salir
                *********************************
                """);
    }

    private static void realizarConversion(String opcion, Scanner scanner) throws Exception {
        String direccion = obtenerDireccion(opcion);
        System.out.println("Ingrese el valor que desea convertir: ");
        String valorStr = scanner.nextLine();
        double valor = Double.parseDouble(valorStr);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(direccion))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

        double valorConvertido;
        if (opcion.equals("2") || opcion.equals("6")) {
            valorConvertido = 1 / conversionRates.get("USD").getAsDouble();
        } else {
            valorConvertido = conversionRates.get(obtenerMonedaDestino(opcion)).getAsDouble();
        }

        double valorFinal = valor * valorConvertido;
        System.out.println("El valor de " + valor + " corresponde al valor final de =>> " + valorFinal);
    }

    private static String obtenerDireccion(String opcion) {
        String monedaBase;
        if (opcion.equals("2") || opcion.equals("6")) {
            monedaBase = "ARS";
        } else {
            monedaBase = "USD";
        }
        return API_BASE_URL + "142c1409d946d66b0b21ff1c/latest/" + monedaBase;
    }

    private static String obtenerMonedaDestino(String opcion) {
        switch (opcion) {
            case "1":
                return "ARS";
            case "2":
                return "USD";
            case "3":
                return "BRL";
            case "4":
                return "USD";
            case "5":
                return "COP";
            case "6":
                return "USD";
            default:
                throw new IllegalArgumentException("Opción no válida: " + opcion);
        }
    }
}
