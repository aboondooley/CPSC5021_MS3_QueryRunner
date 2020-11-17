/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package queryrunner;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

// Updates:

// QueryFrame:
// Updates aesthetics of the GUI - changed the font, text color, background color, and button colors to be earthy
// natural colors because we are a hiking app
// See lines 53-167 in QueryFrame
// and lines 500-508 in QueryFrame

// QueryRunner:
// Created a UI menu for the user to select which queries they want to run, and the menu continues to prompt the user
// until the user signals they want to exit by typing 11
// If the user inputs an invalid option, they are re-prompted until the input is valid
//
// Created a method that prints out the query name once the user has selected a query, in case they forgot which query they selected
//
// Created a method which interacts with the database to run the selected query and retrieve the results.
// It takes care of actions queries, as well as queries with and without parameters.
//
// Created a method to print out the query results to the console in a neat and read-able way. It mimics the formatting
// an SQL table would have with proper indenting and spacing
//
// Created documentation of all new methods as well as in-line comments throughout the code
// See lines 280-544 in QueryRunner

// QueryJDBC:
// Updated the connection so that useSSL is false as it was causing an error
// See line 204 in QueryJDBC

/**
 * 
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 */
public class QueryRunner {

    
    public QueryRunner()
    {
        this.m_jdbcData = new QueryJDBC();
        m_updateAmount = 0;
        m_queryArray = new ArrayList<>();
        m_error="";

        
        // Our app is called Trail Seeker
        this.m_projectTeamApplication="Trail Seeker";

        // Query 1
        m_queryArray.add(new QueryData(
                "Select hiker_fname, hiker_lname, SUM(distance) as full_milage " +
                "from hiker " +
                "left join hike_instance on hiker.hiker_id = hike_instance.hiker_id " +
                "left join trail on hike_instance.trail_id = trail.trail_id " +
                "where hiker.hiker_id = ?",
                new String[] {"HIKER ID"}, new boolean[] {false}, false, true));
        // Query 2
        m_queryArray.add(new QueryData(
                "select hiker_fname, hiker_lname, sum(elevation) as full_elevation " +
                        "from hiker " +
                        "left join hike_instance on hiker.hiker_id = hike_instance.hiker_id " +
                        "left join trail on hike_instance.trail_id = trail.trail_id " +
                        "where hike_instance.hiker_id = ?",
                new String[] {"HIKER ID"}, new boolean[] {false}, false, true));
        // Query 3
        m_queryArray.add(new QueryData(
                "select hiker_fname, hiker_lname, count(*) as full_trail " +
                        "from hiker " +
                        "left join hike_instance on hiker.hiker_id  = hike_instance.hiker_id " +
                        "group by hiker.hiker_id " +
                        "order by full_trail desc " +
                        "limit 5",
                null, null, false, false));
        // Query 4
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, rating, distance, elevation, date_format(hike_instance_date, '%m/%d/%y') as date " +
                        "from hike_instance " +
                        "left join trail on hike_instance.trail_id = trail.trail_id " +
                        "left join mountain_range on trail.mtn_range_id = mountain_range.mtn_range_id " +
                        "left join rating on hike_instance.hike_instance_id = rating.hike_instance_id " +
                        "where trail.trail_id in (" +
                        "select distinct trail_id " +
                        "from hike_instance " +
                        "where hiker_id = ?) " +
                        "order by year(hike_instance_date), month(hike_instance_date)",
                new String[] {"HIKER ID"}, new boolean[] {false}, false, true));
        // Query 5
        m_queryArray.add(new QueryData(
                "select year(invoice_date) as year, month(invoice_date) as month, sum(line_quantity*product_price) as total_product_revenue " +
                "from line " +
                "left join invoice on line.invoice_id = invoice.invoice_id " +
                "left join product on line.product_id = product.product_id " +
                "group by year, month " +
                "order by year, month",
                null, null, false, false));
        // Query 6
        m_queryArray.add(new QueryData(
                "select hiker_fname, hiker_lname, age, gender_name, subscription_type_description " +
                        "from hiker " +
                        "left join gender on hiker.gender_id = gender.gender_id " +
                        "left join subscription on hiker.hiker_id = subscription.hiker_id " +
                        "left join subscription_type on subscription.subscription_type_id " +
                        "where is_active = 1 " +
                        "order by subscription_type_description",
        null, null, false, false));
        // Query 7
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, distance, elevation, state_name " +
                        "from trail " +
                        "left join state on trail.state_abbrv = state.state_abbrv " +
                        "left join mountain_range on trail.mtn_range_id = mountain_range.mtn_range_id " +
                        "where dog_friendly = 1 " +
                        "and trail_id not in (" +
                        "select distinct trail_id " +
                        "from hike_instance " +
                        "where hiker_id = ? ) " +
                        "and mountain_range.mtn_range_id = (" +
                        "select mtn_range_id " +
                        "from hiker " +
                        "where hiker_id = ? )",
        new String[] {"HIKER ID", "HIKER ID"}, new boolean[] {false, false}, false, true));
        // Query 8
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, distance, elevation, state_name " +
                        "from trail " +
                        "left join state on trail.state_abbrv = state.state_abbrv " +
                        "left join mountain_range on trail.mtn_range_id = mountain_range.mtn_range_id " +
                        "where trail_id not in (" +
                        "select distinct trail_id " +
                        "from hike_instance " +
                        "where hiker_id = ? ) " +
                        "and trail.state_abbrv = (select state_abbrv from hiker where hiker_id = ? ) " +
                        "order by distance desc, elevation desc",
         new String[] {"HIKER ID", "HIKER ID"}, new boolean[] {false, false}, false, true));
        // Query 9
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, avg(rating) as average_rating, count(*) as hike_count " +
                        "from hike_instance " +
                        "left join trail on hike_instance.trail_id = trail.trail_id " +
                        "left join mountain_range on trail.mtn_range_id = mountain_range.mtn_range_id " +
                        "left join rating on hike_instance.hike_instance_id = rating.hike_instance_id " +
                        "group by trail.trail_id " +
                        "order by hike_count desc " +
                        "limit 10",
        null, null, false, false));
        // Query 10
        m_queryArray.add(new QueryData(
                "select mtn_range_name, trail_name, distance, elevation, state_name, avg(rating) as average_rating " +
                        "from trail join state using (state_abbrv) " +
                        "join mountain_range using (mtn_range_id) " +
                        "join hike_instance using (trail_id) " +
                        "join rating using (hike_instance_id) " +
                        "where distance between ? and ? " +
                        "group by trail_id " +
                        "order by distance",
        new String[] {"DIST. LOWER LIMIT", "DIST. UPPER LIMIT"}, new boolean[] {false, false}, false, true));
    }

    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }
    
    public int GetParameterAmtForQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }
              
    public String  GetParamText(int queryChoice, int parmnum )
    {
       QueryData e=m_queryArray.get(queryChoice);        
       return e.GetParamText(parmnum); 
    }   

    public String GetQueryText(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    /**
     * Function will return how many rows were updated as a result
     * of the update query
     * @return Returns how many rows were updated
     */
    
    public int GetUpdateAmount()
    {
        return m_updateAmount;
    }
    
    /**
     * Function will return ALL of the Column Headers from the query
     * @return Returns array of column headers
     */
    public String [] GetQueryHeaders()
    {
        return m_jdbcData.GetHeaders();
    }
    
    /**
     * After the query has been run, all of the data has been captured into
     * a multi-dimensional string array which contains all the row's. For each
     * row it also has all the column data. It is in string format
     * @return multi-dimensional array of String data based on the resultset 
     * from the query
     */
    public String[][] GetQueryData()
    {
        return m_jdbcData.GetData();
    }

    public String GetProjectTeamApplication()
    {
        return m_projectTeamApplication;        
    }
    public boolean  isActionQuery (int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }
    
    public boolean isParameterQuery(int queryChoice)
    {
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }
    
     
    public boolean ExecuteQuery(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        return bOK;
    }
    
     public boolean ExecuteUpdate(int queryChoice, String [] parms)
    {
        boolean bOK = true;
        QueryData e=m_queryArray.get(queryChoice);        
        bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        return bOK;
    }   
    
      
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase)
    {

        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }
    
    public boolean Disconnect()
    {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return true;
    }
    
    public String GetError()
    {
        return m_error;
    }

    /**
     * Prints out the UI menu. It shows the client what all of their options are for running queries or exiting.
     */
    public static void PrintMenu() {
        System.out.println("Select query (number) to run: ");
        System.out.println("1  Total distance hiked for user");
        System.out.println("2  Total elevation hiked for user");
        System.out.println("3  Top 5 hikers with the most hikes");
        System.out.println("4  List of all trails user has hiked");
        System.out.println("5  Total product revenue");
        System.out.println("6  List of all current subscriptions");
        System.out.println("7  List of dog-friendly trails the user has not hiked");
        System.out.println("8  List of trails the user has not hiked in their state");
        System.out.println("9  List of top 10 most hiked trails");
        System.out.println("10 List of trails within given distance range");
        System.out.println("11 Exit program");
        System.out.println();
        System.out.println("Selection: ");
    }

    /**
     * Prints out the menu and validates that the users' input is valid. Continues prompting until a valid
     * input is given
     * @param c scanner object to get input from console
     * @return returns the valid menu option that the client selected
     */
    public int MenuChoice(Scanner c)
    {
        PrintMenu();
        String str = c.nextLine();
        while (!str.equals("1")&&!str.equals("2")&&!str.equals("3")&&!str.equals("4")&&!str.equals("5")&&!str.equals("6")
                &&!str.equals("7")&&!str.equals("8")&&!str.equals("9")&&!str.equals("10")&&!str.equals("11")) {
            System.out.println("Invalid choice please enter a number 1-11.");
            System.out.println();
            PrintMenu();
            str = c.nextLine();
        }
        int queryChoice = Integer.parseInt(str);
        return queryChoice;
    }
    /**
     * After the client has chosen a query from the menu, this method prints out the name
     * of the query after the client selects the query so they know which query they selected.
     * @param queryChoice number of query that the client selected
     */
    public void PrintQuery(int queryChoice) {
        switch (queryChoice) {
            case 0:
                System.out.println("Total distance hiked by user");
                break;
            case 1:
                System.out.println("Total elevation hiked by user");
                break;
            case 2:
                System.out.println("Top 5 hikers with the most hikes");
                break;
            case 3:
                System.out.println("List of all trails the user has hiked");
                break;
            case 4:
                System.out.println("Total product revenue");
                break;
            case 5:
                System.out.println("List of all current subscriptions");
                break;
            case 6:
                System.out.println("List of dog-friendly trails that the user has not hiked");
                break;
            case 7:
                System.out.println("List of trails the user has not hiked in their state");
                break;
            case 8:
                System.out.println("List of the top 10 most hiked trails");
                break;
            case 9:
                System.out.println("List of trails within given distance range");
                break;
            default:
                break;
        }

    }

    /**
     * After the query has been run and results returned, this method
     * prints out the results of the query to the console, formatted so it is readable and looks neat
     * A different format is used depending on how many columns are in the return query results
     * @param result a 2D array holding the results
     * @param header a 1D array holding the column names
     * @param queryChoice the query number being displayed
     */
    public void PrintQueryResults(String[][] result, String[] header, int queryChoice) {
        String format;

        switch (queryChoice) {
            case 0: case 1: case 2:
                format = "%-20s%-20s%-10s";
                System.out.format(format, header[0], header[1], header[2]);
                System.out.println();
                for (int k = 0; k < result.length; k++) {
                    System.out.format(format, result[k][0], result[k][1], result[k][2]);
                    System.out.println();
                }
                break;
            case 3: case 9:
                format = "%-25s%-30s%-10s%-10s%-20s%-10s";
                System.out.format(format, header[0], header[1], header[2], header[3], header[4], header[5]);
                System.out.println();
                for (int k = 0; k < result.length; k++) {
                    System.out.format(format, result[k][0], result[k][1], result[k][2], result[k][3], result[k][4], result[k][5]);
                    System.out.println();
                }
                break;
            case 4:
                format = "%-15s%-15s%-20s";
                System.out.format(format, header[0], header[1], header[2]);
                System.out.println();
                for (int k = 0; k < result.length; k++) {
                    System.out.format(format, result[k][0], result[k][1], result[k][2]);
                    System.out.println();
                }
                break;
            case 5:
                format = "%-20s%-20s%-10s%-15s%-30s";
                System.out.format(format, header[0], header[1], header[2], header[3], header[4]);
                System.out.println();
                for (int k = 0; k < result.length; k++) {
                    System.out.format(format, result[k][0], result[k][1], result[k][2], result[k][3], result[k][4]);
                    System.out.println();
                }
                break;
            case 6: case 7:
                format = "%-25s%-30s%-10s%-15s%-15s";
                System.out.format(format, header[0], header[1], header[2], header[3], header[4]);
                System.out.println();
                for (int k = 0; k < result.length; k++) {
                    System.out.format(format, result[k][0], result[k][1], result[k][2], result[k][3], result[k][4]);
                    System.out.println();
                }
                break;
            case 8:
                format = "%-25s%-30s%-18s%-10s";
                System.out.format(format, header[0], header[1], header[2], header[3]);
                System.out.println();
                for (int k = 0; k < result.length; k++) {
                    System.out.format(format, result[k][0], result[k][1], result[k][2], result[k][3]);
                    System.out.println();
                }
                break;
            default:
                break;

        }
    }

    /**
     * After the query name has been printed, this method runs the query by prompting the client for all
     * necessary parameters and talks to the database, and then uses the PrintQueryResults method to format and
     * print out the results
     * @param queryrunner queryrunner object
     * @param c scanner object to get input from client
     * @param queryChoice query number that is being run
     */
    public void RunQuery(QueryRunner queryrunner, Scanner c, int queryChoice) {
        int amt = queryrunner.GetParameterAmtForQuery(queryChoice);
        String[] params = new String[amt];
        if (queryrunner.isParameterQuery(queryChoice)){

            for (int j = 0; j < amt; j++) {
                String label = queryrunner.GetParamText(queryChoice, j);
                System.out.println("Parameter " + (j+1) + " " + label + ": ");
                params[j] = c.nextLine();
            }
        }
        if (queryrunner.isActionQuery(queryChoice)) {
            queryrunner.ExecuteUpdate(queryChoice, params);
            int changed = queryrunner.GetUpdateAmount();
            System.out.println(changed + " rows were updated.");
        } else {
            queryrunner.ExecuteQuery(queryChoice, params);
            String[][] result = queryrunner.GetQueryData();
            String[] header = queryrunner.GetQueryHeaders();
            queryrunner.PrintQueryResults(result, header, queryChoice);
        }
        System.out.println();
    }
 
    private QueryJDBC m_jdbcData;
    private String m_error;    
    private String m_projectTeamApplication;
    private ArrayList<QueryData> m_queryArray;  
    private int m_updateAmount;
            
    /**
     * Main method.
     * Prompts the user to pick either the GUI or console versions.
     * @param args the command line arguments
     */
    

    
    public static void main(String[] args) {
        Scanner c = new Scanner(System.in);

        // allows the user to select GUI or console version when they start the program
        System.out.println("GUI or console version?");
        System.out.println("1 GUI");
        System.out.println("2 Console");
        System.out.println("Enter choice number: ");
        String choice = c.nextLine();

        final QueryRunner queryrunner = new QueryRunner();
        
        if (choice.equals("1")) // GUI version
        {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {

                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        }
        else if (choice.equals("2")) // console version
        {

            // prompts the client for database connection info
                System.out.println("Welcome to the Trail Seeker Console!");
                System.out.println("Enter info to connect to the database.");
                System.out.println("Host name: ");
                String connection = c.nextLine();
                System.out.println("Username: ");
                String username = c.nextLine();
                System.out.println("Password: ");
                String password = c.nextLine();
                System.out.println("Database: ");
                String db = c.nextLine();

                // connect to database
                boolean success = queryrunner.Connect(connection, username, password, db);
                if (success) {

                    // prints menu of queries for client to choose from
                    int queryChoice = queryrunner.MenuChoice(c);

                    // option 11 = exit app
                    // continues to print menu and prompt client for query choice until the client chooses to exit
                    while (queryChoice != 11) {
                        queryChoice--;
                        // prints the query name chosen by the client
                        queryrunner.PrintQuery(queryChoice);
                        // runs the query, prompting the client for parameters, if necessary
                        queryrunner.RunQuery(queryrunner, c, queryChoice);
                        // prompts the client again from the main menu
                        queryChoice = queryrunner.MenuChoice(c);

                    }

                    // prints a good bye message when the client chooses to exit
                    System.out.println("Thanks for using Trail Seeker App Console!");
                    boolean disconnect_success = queryrunner.Disconnect();

                    // prints out the disconnection error if the disconnection is unsuccessful
                    if (!disconnect_success) {
                        System.out.println("Error while disconnecting from the database.");
                        System.out.println("Error: ");
                        System.out.println(queryrunner.GetError());
                    }

                } else {
                    // prints out an error message for the client if the connection is unsuccessful
                    System.out.println("Failed when connecting to the server. Please check login info and try again.");
                    System.out.println("Error: ");
                    System.out.println(queryrunner.GetError());
                }

                
        } else { // clients does not enter either 1 or 2
                System.out.println("Invalid choice, please choose either 1 (GUI) or 2 (console).");
                String[] s = {};
                main(s); // runs main again if incorrect input is entered - running the program again
        }
    }    
}
