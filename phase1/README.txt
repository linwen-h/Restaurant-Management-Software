GROUP_0215
Project Phase 1 README

=== PROJECT SET UP ===

1. Once you download our repository, create a new project with phase1 as the root folder (NOT source root)

2. Our project uses the Maven framework (right click the module, and "Add Framework Support -> tick Maven -> OK")

3. Go into pom.xml, in the bottom right -> "Enable Auto-sync". The project should download all relevant dependencies

3.1 If no such option exists, manually sync the project dependencies

4. Right Click folder resources -> Mark directory as -> Resources Root

5. Right Click folder src -> Mark directory as -> Sources Root

6. Run the program from user.Restaurant.main()

=== CONFIGURATION ===

1. settings.txt contains all the relevant fields for setting up the Restaurant simulation.
    name            : (String)  the name of the restaurant                                                  default: "Lindsey's Pizzeria"
    num_tables      : (int)     number of tables in the restaurant                                          default: 10
    seats_per_table : (int)     number of seats per table (all tables have the same number of seats)        default: 4
    num_cooks       : (int)     number of cooks in the kitchen                                              default: 2
    num_servers     : (int)     number of servers in the restaurant                                         default: 2
    num_managers    : (int)     number of managers in the restaurant                                        default: 1
    fix_console     : (boolean) replace the default out streams (refer to util.Tools.java javadoc)          default: true

    * num_tables, seats_per_table, num_cooks, num_servers, num_managers are used to initialize
        Table, Seat, Cook, Server, and Manager IDs (these are used to reference Tables and
        Employees in your script.)

        Note: all ids are 1-indexed, except for tables, which are 0-indexed.

2. Each order can only handle one MenuItem, however, each table-seat can have more than one order.

3. When referencing Ingredients in events.txt, find the corresponding "name" field of ingredient from
        resources/ingredients.json

4. When referencing MenuItems in events.txt, find the corresponding "name" field of item from
        resources/menu.json

5. To simplify the menu.json file, all menu items use 1 unit of their respective ingredients

6. There is no way to clear requests.txt through the program, unless a shipment is received.

7. events.txt contains the script for runtime.

    a sample script is provided in events.txt that orders 4 orders for 1 table

    a # represents a comment (comments require their own line):
        # this is a comment

    all events are in the general form:
        EVENT_TYPE|<data>

    ORDER: simulates an order
        ORDER|<ORDER ID>/<SERVER ID>/<TABLE-SEAT>/<ITEM>/[ADDITIONS]/[SUBTRACTIONS]

        <ORDER ID>      : Pick a unique 4 digit number to represent this order. (Future events use this ID to find
                            existing orders.
        <SERVER ID>     : The id of the Server that took the order, refer to CONFIGURATION step 1
        <TABLE-SEAT>    : The table and seat that requested the order.
        <ITEM>          : The MenuItem for the order
        [ADDITIONS]     : An array of Ingredients (comma separated) to add to the menu item        (can be anything)
        [SUBTRACTIONS]  : An array of Ingredients (comma separated) to subtract from the menu item (can be anything, even if it isn't part of the item)

        Ex. ORDER|1003/1/5-4/Pork Burger/[BACON]/[LETTUCE]
        Means "Server (id 1) orders order (id 1003) for table 5 seat 4, a Pork Burger with BACON and without LETTUCE"

    DISPATCH: simulates the kitchen automatically passing the order to a cook
        DISPATCH|<ORDER ID>/<COOK ID>

        <ORDER ID>      : The id of the order, when creating the ORDER event
        <COOK ID>       : The id of the cook to dispatch the order to

        Ex. DISPATCH|1003/1
        Means "Order (id 1003) dispatched to cook (id 1)"
        Note: After this event, the cook is not available to receive any more DISPATCH events
              until the cook finishes the order.

    RECEIVE: simulate the cook confirming receiving of order request
        RECEIVE|<ORDER ID>/<COOK ID>

        <ORDER ID>      : The id of the order, when creating the ORDER event
        <COOK ID>       : The id of the cook that receives the order

        Ex. RECEIVE|1003/1
        Means "Cook (id 1) confirms receiving of order (id 1003)"
        Note: The <COOK ID> must be the same cook as in the DISPATCH event for the order

    COOK: simulates the cook cooking the order
        COOK|<ORDER ID>/<COOK ID>

        <ORDER ID>      : The id of the order, when creating the ORDER event
        <COOK ID>       : The id of the cook that cooks the order

        Ex. COOK|1003/1
        Means "Cook (id 1) confirms cooking the order (id 1003)"
        Note: The <COOK ID> must be the same cook as in the DISPATCH event for the order

    DELIVER: simulates the server delivering the order
        DELIVER|<ORDER ID>/<SERVER ID>

        <ORDER ID>      : The id of the order, when creating the ORDER event
        <SERVER ID>     : The id of the server that initially created the ORDER

        Ex. DELIVER|1003/1
        Means "Server (id 1) confirms delivery of order"
        Note: The <SERVER ID> must be the same server as when creating the order

    CANCEL: simulates the customer cancelling the order
        CANCEL|<ORDER ID>

        <ORDER ID>      : The id of the order, when creating the ORDER event

        Ex. CANCEL|1003
        Means "Customer cancelled order (id 1003), remove it from their bill
               and suspend all future events on this order"
        Note: You can cancel the order after it has been delivered,
              since this is up to the discretion of the person that writes the events.txt

    RETURN: simulates the customer returning the order
        RETURN|<ORDER ID>/<REASON>

        <ORDER ID>      : The id of the order, when creating the ORDER event
        <REASON>        : The reason of the return

        Ex. RETURN|1003/Food was undercooked
        Means "Customer returned order (id 1003) because food was undercooked"
        Note: Internally, this creates a CANCEL event and creates another ORDER event, so after returning, you
              must Dispatch, receive, cook, and deliver again.

    DELAY: delays the script for some seconds
        DELAY|<SECONDS>

        <SECONDS>       : The time to suspend the script for

        Ex. DELAY|10
        Means "Delay the script for 10 seconds"

    SHIPMENT: simulates a shipment coming in to the restaurant
        SHIPMENT|{INGREDIENTS}

        {INGREDIENTS}   : A map of all the ingredients in the shipment

        Ex. SHIPMENT|{BACON:10,COFFEE:1000}
        Means "Shipment with 10 units of BACON and 1000 units of COFFEE received."

    LOGINV: prints out the restaurant's current inventory
        LOGINV|<MANAGER ID>

        <MANAGER ID>    : The id of the manager that requested the print out

        Ex. LOGINV|1

    STATISTICS: prints out the restaurant's best sellers
        STATISTICS|<MANAGER ID>

        <MANAGER ID>    : The id of the manager that requested the print out

        Ex. STATISTICS|1

    GETBILL: gets the bill of the table, or seat (depends what you feed it)
        Option 1: GETBILL|<TABLE>

            <TABLE>         : the table to get the bill for

            Ex. GETBILL|5
            Means "Get the bill for table 5"

        Option 2: GETBILL|<TABLE-SEAT>

            <TABLE-SEAT>    : The table and seat number to get the bill for
            Ex. GETBILL|5-4
            Means "Get the bill for table 5, seat 4"

        Note: Internally, GETBILL clears the table's orders. At this point, it is safe to assume the
              table is empty.

    CLEARTABLE: clears the table, and mark it as available for next customers
        CLEARTABLE|<TABLE ID>/<SERVER ID>

        <TABLE ID>      : The id of the table to clear
        <SERVER ID>     : The id of the server that cleared it

        Ex. CLEARTABLE|5/1
        Means "Server (id 1) cleared table (id 5)"
        Note: After this point, all orders on the table are cleared, cannot get bill after this point.

    === IMPORTANT ===================================================================================================
    There is a specific flow of EVENTS that must be processed before another event for an order (RETURN is optional)

        ORDER -> DISPATCH -> RECEIVE -> COOK -> DELIVER (-> RETURN) -> CLEARTABLE
           ^        ^                                        |             |
           |        |                                        v             |
           |        <------------------<----------------------             |
           |                                                               v
           <-----------------------<----NEW CUSTOMERS---<-------------------

        NOTE: The other events can be accessed anytime in the script, provided that all references are initialized
    =================================================================================================================

=== UML ===
UML.pdf doesn't get rendered properly on some browsers (fuzzy, missing lines)
We've included a UML.png with full resolution.

Our UML follows the IntelliJ UML style guide (a key is attached as key.png):
    -- Red arrows indicate inner class
    -- Dark blue arrows indicate inheritance
