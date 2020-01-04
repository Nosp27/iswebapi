package com.ashikhmin.admin.consoleAdmin;

import com.ashikhmin.model.Category;
import com.ashikhmin.model.Facility;
import com.ashikhmin.model.Region;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class Admin {
    private static Scanner sc;

    static final String SERVER = "http://192.168.1.56:8080";

    static final String GET_ALL_REGIONS = "/regions";
    static final String GET_REGION = "/region";
    static final String GET_ALL_CATEGORIES = "/categories";
    static final String GET_CRITERIZED_FACILITIES = "/facilities/";
    static final String READ_IMAGE_SUFFIX = "/image/";

    private static ObjectMapper mapper = new ObjectMapper();

    private Admin() {
    }

    public static void main(String[] args) {
        sc = new Scanner(System.in);
        while (true) {
            String cmd = sc.nextLine();
            if ("exit".equals(cmd))
                break;
            try {
                Method method = Admin.class.getDeclaredMethod(cmd);
                method.invoke(null);
            } catch (NoSuchMethodException e) {
                System.out.println("mistaken. no method " + cmd);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.print("Try again, ");
                System.out.println(e.getCause().getLocalizedMessage());
            }
        }
        sc.close();
    }

    private static void help() {
        System.out.println(
                "\n*****This is the console admin for web api******" +
                        "\ncommands are the following:" +
                        "\n\t--help: display this message" +
                        "\n\t--insertFacility: insert facility" +
                        "\n\t--insertRegion: insert region" +
                        "\n\t--insertCategory: insert category\n"
        );
    }

    private static Facility insertFacility() {
        Facility f = new Facility();
        try {
            enterProperty("Id", Integer::parseInt, f::set_id);
            enterProperty("Name", x -> x, f::setName);
            enterProperty("Description", x -> x, f::setDescription);
            enterProperty(
                    "Coords",
                    x -> Arrays.stream(x.split(", ", 2)).map(Double::parseDouble).collect(Collectors.toList()),
                    x -> {
                        if (x.size() != 2) return;
                        f.setLat(x.get(0));
                        f.setLng(x.get(1));
                    });
            enterProperty("Image", Integer::parseInt, f::setImageId);
            send(f, "/facility");
            System.out.println("Fine");
            return f;
        } catch (IOException e) {
            System.out.println("Not fine");
            throw new RuntimeException(e);
        }
    }

    private static Region insertRegion() {
        Region ret = new Region();
        try {
            enterProperty("Id", Integer::parseInt, ret::setRegionId);
            enterProperty("Name", x -> x, ret::setRegionName);
            enterProperty("Image", Integer::parseInt, ret::setImageId);
            send(ret, "/region");
            System.out.println("Fine");
            return ret;
        } catch (IOException e) {
            System.out.println("Not fine");
            throw new RuntimeException(e);
        }
    }

    private static Category insertCategory() {
        Category ret = new Category();
        try {
            enterProperty("Name", x -> x, ret::setCatName);
            enterProperty("Image", Integer::parseInt, ret::setImageId);
            send(ret, "/category");
            System.out.println("Fine");
            return ret;
        } catch (IOException e) {
            System.out.println("Not fine");
            throw new RuntimeException(e);
        }
    }

    private static String insertImage() {
        StringBuilder path = new StringBuilder();
        StringBuilder id = new StringBuilder();
        try {
            enterProperty("Path", x -> x, path::append);
            enterProperty("Image id", x -> x, id::append);

            send(path.toString(), "/category/" + id);
            System.out.println("Fine");
            return id.toString();
        } catch (IOException e) {
            System.out.println("Not fine: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static <T> void enterProperty(String propertyName, Function<String, T> parser, Consumer<T> setter)
            throws IOException {
        do {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(SERVER + "/regions").openConnection();
                if (conn.getResponseCode() != 200)
                    throw new IOException("ping failed");
                System.out.print(propertyName + ": ");
                String line = sc.nextLine();
                T value;
                if (line.isEmpty())
                    value = null;
                else
                    value = parser.apply(line);
                setter.accept(value);
            } catch (IllegalArgumentException e) {
                System.out.println();
                continue;
            }
            System.out.println();
            break;
        } while (true);
    }

    private static void send(Object value, String suffix) throws IOException {
        byte[] bts = mapper.writer().writeValueAsBytes(value);
        HttpURLConnection conn = (HttpURLConnection) new URL(SERVER + suffix).openConnection();
        conn.addRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            out.write(bts);
            int resp = conn.getResponseCode();
            if (resp != 200) {
                byte[] bytes = new byte[conn.getErrorStream().available()];
                conn.getErrorStream().read(bytes);
                throw new IOException("Unsuccessful response: " + resp + ".\n" + new String(bytes));
            }
        }
    }
}
