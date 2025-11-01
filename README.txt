# README.txt

## Overview

This project implements an enhanced **Transportation Fleet Management System (Assignment 2)** using advanced Java OOP and Collections Framework concepts. 
It builds upon Assignment 1 by introducing sorting, searching, and collection-based data management features in `FleetManager` and `Main`.

The system demonstrates inheritance, polymorphism, abstract classes, interfaces, exception handling, and now extensively uses Java Collections such as `ArrayList`, `Set`, 'TreeSet', `Map`, and `Comparator`.

## New Additions in Assignment 2

* **Collections Framework Usage:**
  - `ArrayList<Vehicle>` for the fleet
  - `HashSet<String>` to track distinct model names
  - `TreeSet` view for sorted distinct model listing
  - `Comparator` for sorting vehicles by speed, model, or efficiency

* **Enhanced Fleet Operations:**
  - Sort fleet by **speed**, **model**, or **fuel efficiency**
  - Display **fastest and slowest vehicles**
  - Show **distinct model names** in the fleet (using a `Set`)
  - IO streams for saving/loading csv file

* **Persistent Data Management:**
  - `saveToFile()` and `loadFromFile()` to manage fleet data in CSV format.

* **User Interface (CLI):**
  - Added menu options:
    12. Sort Fleet (by speed/model/efficiency)
    13. Show Fastest and Slowest Vehicles
    14. Display Distinct Models

## OOP Concepts Demonstrated

* **Inheritance:**  
  `Vehicle` is the base class; `LandVehicle`, `AirVehicle`, `WaterVehicle` are abstract subclasses; concrete types include `Car`, `Truck`, `Bus`, `Airplane`, and `CargoShip`.

* **Abstraction:**  
  `Vehicle` declares abstract methods (`move`, `calculateFuelEfficiency`, `estimateJourneyTime`), implemented by concrete subclasses.

* **Interfaces:**  
  - `FuelConsumable`, `CargoCarrier`, `PassengerCarrier`, `Maintainable` define contracts for specific behaviors.
  - Implemented across classes as needed.

* **Polymorphism:**  
  - `FleetManager` handles a `List<Vehicle>` but operates polymorphically on various vehicle types.
  - Interfaces and abstract methods allow flexible behavior.

* **Encapsulation:**  
  - Private data members with public getters and setters.
  - Controlled access through well-defined interfaces.

* **Custom Exceptions:**  
  - `OverloadException`, `InvalidOperationException`, `InsufficientFuelException` handle logical and runtime errors gracefully.

## Collections Used and Justification

This project intentionally uses multiple collection types from the Java Collections Framework to solve different problems efficiently and express intent clearly. Below is a breakdown of what collections are used, where they appear in the code, why each was chosen, and the complexity tradeoffs.

1. * **ArrayList<Vehicle> fleet** — canonical storage of vehicles

    -Where: FleetManager (primary field).

    -Why: An ArrayList is the natural choice for an ordered, indexable collection with fast iteration. The fleet is frequently iterated (report generation, start journeys, display), and ArrayList offers O(1) access by index and very fast sequential traversal (cache-friendly). We intentionally keep the fleet order stable unless the user chooses to re-sort; this allows both persistent ordering and non-destructive sorted views.

    -Complexity: add/remove (amortized O(1) for add, O(n) for removal by value), iteration O(n), sort O(n log n).



2. * **HashSet<String> modelSet** — distinct model names (uniqueness)

    -Where: FleetManager (tracks model string values).

    -Why: The assignment requires demonstrating distinct model handling. A HashSet enforces uniqueness and provides average O(1) membership tests when adding/checking models. When the user loads from CSV, the modelSet is rebuilt from the loaded vehicles so it is always consistent.

    -Complexity: add/contains/remove O(1) average.



3. * **TreeSet<String>** — ordered view of distinct models (alphabetical)

    -Where: returned by getDistinctModels() as new TreeSet<>(modelSet) (call-site view).

    -Why: TreeSet provides an automatically sorted set (natural order). We do not maintain two persistent sets (HashSet + TreeSet) to avoid overhead; instead we keep a HashSet as the canonical distinct model store and create a TreeSet copy on-demand when an ordered (A→Z) view is requested. This balances runtime speed for updates and neat, sorted outputs for users.

    -Complexity: copying to TreeSet is O(m log m) where m = number of unique models. This is acceptable because the number of distinct models is typically small relative to the fleet.



4. * **Map<String,String>** (LinkedHashMap used in startAllJourneys) — per-journey results

    -Where: startAllJourneys() returns a Map<String,String> (implemented as LinkedHashMap to preserve insertion order).

    -Why: We need to return a mapping from vehicle ID to result ("Ok" or "failed: ...") in a predictable order. A map provides direct ID→result lookups. Using LinkedHashMap ensures the results are printed in the same order the method processed vehicles (deterministic output for testing).

    -Complexity: O(1) average put/get.


## File I/O and Persistence Details

This section explains how saveToFile() and loadFromFile() are implemented, including CSV schema, parsing rules, validation, atomic loading behavior, and error handling. The goal is predictable, testable persistence with clear user feedback and robustness to malformed input.

* **High-level approach**

CSV format is used for human-readable, editable persistence. Each line begins with a vehicle type token (Car, Truck, Bus, Airplane, CargoShip) followed by common fields and type-specific fields in a fixed order.

saveToFile(filename): writes the current fleet to a file line-by-line in the canonical CSV schema. It uses try-with-resources to guarantee the file is closed properly.

loadFromFile(filename): reads the file line-by-line, parses tokens, uses a VehicleFactory helper to create concrete vehicle objects, collects them in a temporary list, and only after successful parsing replaces the in-memory fleet. This makes loading atomic — if the file is partially corrupt we skip bad lines but will not corrupt the currently loaded fleet.

* **File Writing Details**
    -saveToFile() writes each vehicle line using PrintWriter.printf() to ensure formatting consistency (fixed decimal formats for numeric fields). It uses try-with-resources to automatically close the writer and will propagate IOException to the caller (CLI catches this and prints a friendly message).


## Compile Instructions

* **Windows Powershell**
$files = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d bin $files

java -cp bin app.Main

## Example CLI flow 

1. Add Vehicle → Choose type, input properties
3. Start Journey → Enter distance
12. Sort Fleet → Choose speed/model/efficiency
13. Show Fastest/Slowest → Displays top/bottom
14. Display Distinct Models → Lists all unique models
7. Save Fleet → Exports to CSV
8. Load Fleet → Imports from CSV

## Example output 

Choose an option: 12
Sort by: 1=Speed 2=Model 3=Fuel Efficiency
Choose: 1
Fleet sorted by max speed (fastest first).

Current Fleet (sorted):
------------------------------------------------
Vehicle Info:
ID: 456
Model: indigo
Max Speed: 900.00 km/h
Current Mileage: 34.00 km
------------------------------------------------
Vehicle Info:
ID: 345
Model: toyota
Max Speed: 400.00 km/h
Current Mileage: 23.00 km
------------------------------------------------
Vehicle Info:
ID: 123
Model: bmw
Max Speed: 340.00 km/h
Current Mileage: 23.00 km
------------------------------------------------

Choose an option: 13

Fastest Vehicle:
Vehicle Info:
ID: 456
Model: indigo
Max Speed: 900.00 km/h
Current Mileage: 34.00 km

Slowest Vehicle:
Vehicle Info:
ID: 123
Model: bmw
Max Speed: 340.00 km/h
Current Mileage: 23.00 km

Choose an option: 14
Distinct Models in Fleet:
 - bmw
 - indigo
 - toyota

 ##THANK YOU



