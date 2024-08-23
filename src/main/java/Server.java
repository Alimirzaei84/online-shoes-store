import javax.print.DocFlavor;
import java.io.*;
import java.net.*;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashMap;
import java.util.Map;

public class Server {
    static Map<String, Integer> inventory = new HashMap<>();
    static Map<String, Customer> customers = new HashMap<>();

    public static Map<String, Customer> getCustomers() {
        return customers;
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            inventory.put("shoe1", 5);
            inventory.put("shoe2", 5);
            inventory.put("shoe3", 5);
            System.out.println("Server started");

            while (true) {
                Socket socket = server.accept(); // Accept client connection
                System.out.println("Client connected: " + socket.getInetAddress());

                // Create a new thread to handle this client
                Thread clientThread = new Thread(new ClientHandler(socket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;
    private Customer currentCustomer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        currentCustomer = null;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String clientMessage;
            while (true) {
                clientMessage = in.readUTF(); // Read client input
                if (clientMessage.equals("Over")) {
                    break;
                }
                out.writeUTF(processInput(clientMessage));
            }
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized String processInput(String input) {
        if (Command.REGISTER.matches(input)) return register(input);
        else if (Command.LOGIN.matches(input)) return login(input);
        else if (Command.LOGOUT.matches(input)) return logout();
        else if (Command.GET_PRICE.matches(input)) return getPrice(input);
        else if (Command.GET_QUANTITY.matches(input)) return getQuantity(input);
        else if (Command.GET_MONEY.matches(input)) return getMoney();
        else if (Command.CHARGE_MONEY.matches(input)) return chargeMoney(input);
        else if (Command.PURCHASE_SHOES.matches(input)) return purchaseShoes(input);
        else return "!! Invalid command !!";
    }

    private synchronized String register(String input) {
        String id = Command.REGISTER.getGroup(input, "id");
        String name = Command.REGISTER.getGroup(input, "name");
        int money = Integer.parseInt(Command.REGISTER.getGroup(input, "money"));

        if (Server.customers.containsKey(id)) return "Already exists!";
        else {
            Customer customer = new Customer(name, id, money);
            Server.customers.put(id, customer);
            return "Customer created successfully";
        }
    }

    private synchronized String login(String input) {
        if (currentCustomer != null) return "You should first logout";
        else {
            String id = Command.LOGIN.getGroup(input, "id");
            if (!Server.customers.containsKey(id)) {
                return "No User with ID : " + id;
            } else {
                currentCustomer = Server.customers.get(id);
                return "Logged in successfully";
            }
        }
    }

    private synchronized String logout() {
        if (currentCustomer == null) return "No user Logged in";
        currentCustomer = null;
        return "Logged out successfully";
    }

    private synchronized String getPrice(String input) {
        String shoeName = Command.GET_PRICE.getGroup(input, "shoeName");
        return switch (shoeName) {
            case "shoe1" -> "Price: 10$";
            case "shoe2" -> "Price: 20$";
            case "shoe3" -> "Price: 30$";
            default -> "Shoes name is invalid";
        };
    }

    private synchronized String getQuantity(String input) {
        String shoeName = Command.GET_QUANTITY.getGroup(input, "shoeName");
        if (!Server.inventory.containsKey(shoeName)) return "Invalid shoes name";
        return "We have " + Server.inventory.get(shoeName) + " number of " + shoeName;
    }

    private synchronized String getMoney() {
        if (currentCustomer == null) return "Please login first!";
        return "You have " + currentCustomer.getMoney() + "$ right now!";
    }

    private synchronized String chargeMoney(String input) {
        if (currentCustomer == null) return "Please login first!";
        currentCustomer.setMoney(currentCustomer.getMoney() + Integer.parseInt(Command.CHARGE_MONEY.getGroup(input, "money")));
        return "Your money increased to " + currentCustomer.getMoney() + "$";
    }

    private synchronized int getShoesPrice(String shoesName) {
        return switch (shoesName) {
            case "shoe1" -> 10;
            case "shoe2" -> 20;
            case "shoe3" -> 30;
            default -> -1;
        };
    }

    private synchronized String purchaseShoes(String input) {
        if (currentCustomer == null) return "Please login first!";
        String shoeName = Command.PURCHASE_SHOES.getGroup(input, "shoeName");
        int quantity = Integer.parseInt(Command.PURCHASE_SHOES.getGroup(input, "quantity"));
        if (!Server.inventory.containsKey(shoeName)) return "Invalid shoe name!";
        if (Server.inventory.get(shoeName) < quantity)
            return "we do not have " + quantity + " number of the " + shoeName;
        if (quantity * getShoesPrice(shoeName) > currentCustomer.getMoney())
            return "You don't have enough money to buy " + quantity + " number of " + shoeName;
        currentCustomer.setMoney(currentCustomer.getMoney() - quantity * getShoesPrice(shoeName));
        Server.inventory.put(shoeName, Server.inventory.get(shoeName) - quantity);
        return "Purchased successfully";
    }

}


class Customer {
    private String name;
    private String id;
    private int money;

    public Customer(String name, String id, int money) {
        this.name = name;
        this.id = id;
        this.money = money;
    }


    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}