import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class BankAccounts {
    private Set<Integer> bankAccntNo = new HashSet<>();
    private Map<Integer, Integer> pins = new HashMap<>();
    private Map<Integer, Double> balance = new HashMap<>();

    public boolean addAccount(int num, int pin) {
        if (bankAccntNo.contains(num))
            return false;
        ATM_Interface.details.initiateHistory(num);
        balance.put(num, 5000.00);// initial deposit amount is set as Rs5000
        bankAccntNo.add(num);
        pins.put(num, pin);
        return true;
    }

    public boolean checkAccntNo(int actNo) {
        if (!bankAccntNo.contains(actNo)) {
            return false;
        }
        return true;
    }

    public double getBalance(int acn) {
        return (double) balance.get(acn);
    }

    public void updateBalance(int acn, double bal) {
        balance.put(acn, bal);
    }

    public boolean checkPin(int actNo, int pin) {
        return pins.getOrDefault(actNo, -1) == pin ? true : false;
    }

}

class UserInterface {
    int actNo;

    public void loggingIn() {
        System.out.println("Enter Account No : ");
        actNo = ATM_Interface.sc.nextInt();
        if (!ATM_Interface.baccnt.checkAccntNo(actNo)) {
            System.out.println("INVALID ACCOUNT NO !! PLEASE TRY AGAIN");
            ATM_Interface.atm.menu();
        } // checking account number is valid or
        System.out.println("Enter Pin");
        int pin = ATM_Interface.sc.nextInt();
        if (!ATM_Interface.baccnt.checkPin(actNo, pin)) {
            System.out.println("INVALID PIN");
            ATM_Interface.atm.menu();
        }
        secondMenu();

    }

    public void secondMenu() {
        try {
            System.out.println("What would You Like to Do");
            System.out.println("1 . Transactions History");
            System.out.println("2 . Withdraw");
            System.out.println("3 . Deposit");
            System.out.println("4 . Transfer");
            System.out.println("5 . Logout");
            int choice = ATM_Interface.sc.nextInt();
            switch (choice) {
                case 1:
                    ATM_Interface.details.getHistory(actNo);
                    break;
                case 2:
                    ATM_Interface.toDo.withdraw(actNo);
                    break;
                case 3:
                    ATM_Interface.toDo.deposit(actNo);
                    break;
                case 4:
                    ATM_Interface.toDo.transfer(actNo);
                    break;
                case 5:
                    ATM_Interface.atm.menu();
                    break;
                default:
                    new InputMismatchException("INVALID INPUT!!");
            }
        } catch (InputMismatchException e) {
            System.out.println("\n" + e.getMessage() + "\n");
            secondMenu();
        }
    }

    public void register() {
        System.out.println("Enter Account No to be Registered");
        int acnt = ATM_Interface.sc.nextInt();
        System.out.println("Enter Pin");
        int pin = ATM_Interface.sc.nextInt();
        boolean makeAccnt = ATM_Interface.baccnt.addAccount(acnt, pin);
        if (!makeAccnt) {
            System.out.println("Account No already exists in DataBase!");

        } else {
            System.out.println("SUCCESFULLY MADE AN ACCOUNT!!");
        }
        ATM_Interface.atm.menu();
    }

}

class BankDetails {
    private Map<Integer, StringBuilder> transactionHistory = new HashMap<>();;
    StringBuilder trn = new StringBuilder("");
    public static DecimalFormat df = new DecimalFormat("#,###.##");

    public void getHistory(int acnt) {
        System.out.println("\tTRANSACTION HISTORY!\n");
        System.out.println(transactionHistory.get(acnt));
        ATM_Interface.ui.secondMenu();
    }

    public void updateHistory(int acnt, int type, double ammount) {
        trn = transactionHistory.get(acnt);
        switch (type) {
            case 1:
                trn.append("\n-Rs" + df.format(ammount) + "\tWithdrawn\n");
                break;
            case 2:
                trn.append("\n+Rs" + df.format(ammount) + "\tDeposited\n");
                break;
            case 3:
                trn.append("\n-Rs" + df.format(ammount) + "\tTransfered\n");
                break;
            case 4:
                trn.append("\n+Rs" + df.format(ammount) + "\tTransfered\n");
                break;
            default:
                break;
        }
        transactionHistory.put(acnt, trn);
    }

    public void initiateHistory(int acntNo) {
        trn.append("\n+Rs5,000.00\tInitial Deposit\n");
        transactionHistory.put(acntNo, trn);
    }

}

class Transactions {

    double balance;

    public void withdraw(int acnt) {
        balance = ATM_Interface.baccnt.getBalance(acnt);
        System.out.println("Enter Amount to be withdrawn");
        double withD = ATM_Interface.sc.nextDouble();
        if (balance >= withD) {
            ATM_Interface.baccnt.updateBalance(acnt, balance - withD);
            ATM_Interface.details.updateHistory(acnt, 1, withD);
            System.out.println("Sceesfully withdawn Rs" + BankDetails.df.format(withD));

        } else {
            System.out.println("Insufficient Balance!\nCuurent Balance = Rs" + BankDetails.df.format(balance));
        }
        ATM_Interface.ui.secondMenu();
    }

    public void deposit(int acnt) {

        balance = ATM_Interface.baccnt.getBalance(acnt);
        System.out.println("Enter Amount to be Deposited");
        double dep = ATM_Interface.sc.nextDouble();
        ATM_Interface.baccnt.updateBalance(acnt, balance + dep);
        ATM_Interface.details.updateHistory(acnt, 2, dep);
        System.out.println("Sceesfully withdawn Rs" + BankDetails.df.format(dep));
        ATM_Interface.ui.secondMenu();

    }

    public void transfer(int acnt) {
        balance = ATM_Interface.baccnt.getBalance(acnt);
        System.out.println("Enter Amount to be Tranfered");
        double trans = ATM_Interface.sc.nextDouble();
        System.out.println("Enter Acoount No to which the amount is to be tranfered ");
        int scndAcnt = ATM_Interface.sc.nextInt();
        boolean ckscnd = ATM_Interface.baccnt.checkAccntNo(scndAcnt);
        if (!ckscnd) {
            System.out.println("INVALID ACCOUNT NUMBER TO TRANSFER TO!");
            ATM_Interface.ui.secondMenu();
        }
        if (balance >= trans) {
            ATM_Interface.baccnt.updateBalance(acnt, balance - trans);
            ATM_Interface.baccnt.updateBalance(scndAcnt, ATM_Interface.baccnt.getBalance(scndAcnt) + trans);
            ATM_Interface.details.updateHistory(acnt, 3, trans);
            ATM_Interface.details.updateHistory(scndAcnt, 4, trans);
            System.out.println("Sceesfully Transfered Rs" + BankDetails.df.format(trans));

        } else {
            System.out.println("Insufficient Balance!\nCuurent Balance = Rs" + BankDetails.df.format(balance));
        }
        ATM_Interface.ui.secondMenu();
    }

}

public class ATM_Interface {
    public static final Scanner sc = new Scanner(System.in);
    public static UserInterface ui = new UserInterface();
    public static ATM_Interface atm = new ATM_Interface();
    public static BankDetails details = new BankDetails();
    public static BankAccounts baccnt = new BankAccounts();
    public static Transactions toDo = new Transactions();

    public static void main(String[] args) {

        System.out.println(
                "Welcome to the Automated Teller Machine\n");
        atm.menu();
    }

    public void menu() {
        try {
            System.out.println("What would you like to do");
            System.out.println("1 . Login");
            System.out.println("2 . Register");
            System.out.println("3 . Quit");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    new InputMismatchException("Invalid Choice");

            }
        } catch (InputMismatchException e) {
            System.out.println("\n" + e.getMessage() + "\n");
            menu();
        }
    }

    public void login() {
        ui.loggingIn();
    }

    public void register() {
        ui.register();
    }

}
