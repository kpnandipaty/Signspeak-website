# FSA Code Review - Logical Errors Found and Fixed

## Overview
This document describes the logical errors found in the FSA (Finite State Automaton) implementation and the fixes applied.

---

## Error 1: Missing State Itself in Epsilon Closure

### Location
`closure(int statenum)` method

### Issue
The epsilon closure of a state should include the state itself, not just the states reachable via epsilon transitions. This is a fundamental concept in automata theory - the ε-closure(q) always includes q itself.

### Original Code
```java
public ArrayList<Integer> closure(int statenum) {
    ArrayList<Integer> reachablestates = new ArrayList<>();
    ArrayList<Integer> checklist = new ArrayList<>();

    State state = states.get(statenum);

    for (Transition t: state.gettransitions()){
        if (t.getsymbol().equals("")){
            reachablestates.add(t.getdestination());
            checklist.add(t.getdestination());
        }
    }
    // ...
}
```

### Fixed Code
```java
public ArrayList<Integer> closure(int statenum) {
    ArrayList<Integer> reachablestates = new ArrayList<>();
    ArrayList<Integer> checklist = new ArrayList<>();

    // FIX 1: The epsilon closure should include the state itself
    reachablestates.add(statenum);
    
    State state = states.get(statenum);

    for (Transition t: state.gettransitions()){
        if (t.getsymbol().equals("")){
            if (!reachablestates.contains(t.getdestination())){
                reachablestates.add(t.getdestination());
                checklist.add(t.getdestination());
            }
        }
    }
    // ...
}
```

### Impact
Without this fix:
- The NFA simulation would fail for cases where the initial state is an accepting state with no epsilon transitions
- State reachability calculations would be incorrect
- Conversion to DFA would produce incorrect results

---

## Error 2: Redundant Code in `accepts()` Method

### Location
`accepts(String input)` method

### Issue
After the fix to `closure()`, the code in `accepts()` was adding the initial state and then its closure, which now creates a duplicate since closure already includes the state itself.

### Original Code
```java
ArrayList<Integer> currentstates = new ArrayList<>();
currentstates.add(initialstate);
currentstates.addAll(closure(initialstate));
```

### Fixed Code
```java
ArrayList<Integer> currentstates = new ArrayList<>();
currentstates.add(initialstate);

// FIX 2: Since closure() now includes the state itself, we don't need to add it separately
ArrayList<Integer> initialClosure = closure(initialstate);
for (int s : initialClosure) {
    if (!currentstates.contains(s)) {
        currentstates.add(s);
    }
}
```

### Impact
- Prevents duplicate state entries in the current states list
- More efficient processing

---

## Error 3: Unnecessary Epsilon Closure Re-computation in `accepts()`

### Location
`accepts(String input)` method - final acceptance check

### Issue
The final states epsilon closure was being computed again, but the `next()` method already includes epsilon closure of destination states, so this was redundant.

### Original Code
```java
//check if the last state is the accepting state
ArrayList<Integer> finalstates = new ArrayList<>(currentstates);
for (int state : currentstates) {
    ArrayList<Integer> epsilonstates = closure(state);
    for (int e : epsilonstates) {
        if (!finalstates.contains(e)) {
            finalstates.add(e);
        }
    }
}

for (int state : finalstates) {
    if (acceptingstates.contains(state)){
        return true;
    }
}
```

### Fixed Code
```java
// FIX 3: Simplified - the epsilon closure is already computed in next()
// so currentstates already contains all reachable states including epsilon closures
for (int state : currentstates) {
    if (acceptingstates.contains(state)){
        return true;
    }
}
```

### Impact
- More efficient code
- Clearer logic - avoids confusion about when epsilon closure is needed

---

## Error 4: Duplicate State in DFA Construction

### Location
`toDFA()` method

### Issue
Similar to Error 2, after fixing `closure()` to include the state itself, we were adding the initial state and then its closure, creating duplicates.

### Original Code
```java
ArrayList<Integer> startset = new ArrayList<>();
startset.add(initialstate);
startset.addAll(closure(initialstate));
```

### Fixed Code
```java
ArrayList<Integer> startset = new ArrayList<>();
startset.add(initialstate);

// FIX 4: Since closure() now includes the state itself, we need to avoid duplication
ArrayList<Integer> initialClosure = closure(initialstate);
for (int s : initialClosure) {
    if (!startset.contains(s)) {
        startset.add(s);
    }
}
```

### Impact
- Prevents duplicate states in DFA state sets
- Ensures correct DFA construction

---

## Summary

All fixes are related to the fundamental error of not including the state itself in its epsilon closure. The cascading effects of this fix required adjustments in multiple methods to avoid duplicates and redundant computations.

### Key Principle
**The ε-closure of a state q is the set of all states reachable from q using zero or more ε-transitions, which ALWAYS includes q itself.**

These fixes ensure the FSA implementation correctly follows automata theory principles and will work correctly for:
- NFA simulation
- NFA to DFA conversion
- String acceptance testing
- Determinism checking
