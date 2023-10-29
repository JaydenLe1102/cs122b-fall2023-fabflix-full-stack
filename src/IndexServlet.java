import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static utils.ServletUtils.checkLogin;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkLogin(request, response);

        HttpSession session = request.getSession();
        JsonObject responseJsonObject = new JsonObject();

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        ArrayList<String> quantities = (ArrayList<String>) session.getAttribute("quantities");

        if (previousItems == null || quantities == null || previousItems.isEmpty() || quantities.isEmpty()) {
            responseJsonObject.addProperty("message", "No movies in the shopping list.");
        } else {
            JsonArray previousItemsJsonArray = new JsonArray();
            for (int i = 0; i < previousItems.size(); i++) {
                JsonObject itemObject = new JsonObject();
                itemObject.addProperty("item", previousItems.get(i));
                itemObject.addProperty("quantity", quantities.get(i));
                previousItemsJsonArray.add(itemObject);
            }

            responseJsonObject.add("previousItems", previousItemsJsonArray);
        }

        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkLogin(request, response);

        String item = request.getParameter("item");
        String quantityStr = request.getParameter("quantity");

        int quantity = 1;
        if (quantityStr != null && !quantityStr.isEmpty()) {
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                // Handle parsing exception here if needed
            }
        }

        HttpSession session = request.getSession();
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        ArrayList<String> quantities = (ArrayList<String>) session.getAttribute("quantities");

        if (previousItems == null) {
            previousItems = new ArrayList<>();
            quantities = new ArrayList<>();
        }

        int index = previousItems.indexOf(item);
        if (index != -1) {
            int updatedQuantity = Integer.parseInt(quantities.get(index));

            if (quantity > 0) {
                updatedQuantity += 1; // Increment quantity by 1
            } else if (quantity < 0 && updatedQuantity > 1) {
                updatedQuantity -= 1; // Decrement quantity by 1, not exceeding 1
            }

            quantities.set(index, String.valueOf(updatedQuantity));
        } else {
            previousItems.add(item);
            quantities.add(String.valueOf(quantity));
        }

        session.setAttribute("previousItems", previousItems);
        session.setAttribute("quantities", quantities);

        JsonObject responseJsonObject = new JsonObject();
        JsonArray previousItemsJsonArray = new JsonArray();
        for (int i = 0; i < previousItems.size(); i++) {
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("item", previousItems.get(i));
            itemJson.addProperty("quantity", quantities.get(i));
            previousItemsJsonArray.add(itemJson);
        }
        responseJsonObject.add("previousItems", previousItemsJsonArray);
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles DELETE requests to remove an item from the session
     */
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        checkLogin(request, response);

        String item = request.getParameter("item");
        HttpSession session = request.getSession();

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        ArrayList<String> quantities = (ArrayList<String>) session.getAttribute("quantities");

        if (previousItems != null && quantities != null) {
            int index = previousItems.indexOf(item);
            if (index != -1) {
                previousItems.remove(index);
                quantities.remove(index);
            }
        }

        JsonObject responseJsonObject = new JsonObject();
        JsonArray previousItemsJsonArray = new JsonArray();
        for (int i = 0; i < previousItems.size(); i++) {
            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("item", previousItems.get(i));
            itemJson.addProperty("quantity", quantities.get(i));
            previousItemsJsonArray.add(itemJson);
        }
        responseJsonObject.add("previousItems", previousItemsJsonArray);
        response.getWriter().write(responseJsonObject.toString());
    }
}