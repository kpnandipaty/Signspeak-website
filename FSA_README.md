# FSA (Finite State Automaton) Implementation

## Overview
This directory contains a corrected Java implementation of a Finite State Automaton (FSA) that supports both deterministic (DFA) and non-deterministic (NFA) automata, including epsilon (ε) transitions.

## Files

### Implementation
- **FSA.java** - The main FSA class with corrected logic for:
  - Adding states and transitions
  - Computing epsilon closures
  - Simulating NFA execution
  - Converting NFA to DFA
  - Checking if an automaton is deterministic

### Testing
- **FSATest.java** - Comprehensive test suite that validates:
  - Epsilon closure correctness
  - Empty string acceptance
  - NFA simulation with epsilon transitions
  - Determinism checking
  - NFA to DFA conversion

### Documentation
- **FSA_FIXES.md** - Detailed documentation of all logical errors found and fixed
- **FSA_REVIEW_SUMMARY.md** - High-level summary of the review process and results

## Key Features

### Epsilon Transitions
The implementation correctly handles epsilon (ε) transitions, represented by empty strings ("").

### Epsilon Closure
The `closure()` method correctly computes the ε-closure of a state, which includes:
- The state itself
- All states reachable via zero or more ε-transitions

### NFA Simulation
The `accepts()` method simulates NFA execution by tracking all possible states at each step.

### NFA to DFA Conversion
The `toDFA()` method implements the subset construction algorithm to convert an NFA to an equivalent DFA.

## Usage

### Compiling
```bash
javac FSA.java
javac FSATest.java
```

### Running Tests
```bash
java FSATest
```

### Creating an NFA
```java
FSA nfa = new FSA();

// Add states (startingState, acceptingState)
int q0 = nfa.addState(true, false);   // initial state
int q1 = nfa.addState(false, false);  // intermediate state
int q2 = nfa.addState(false, true);   // accepting state

// Add transitions (from, symbol, to)
nfa.addTransition(q0, "", q1);     // epsilon transition
nfa.addTransition(q1, "a", q2);    // transition on 'a'

// Test string acceptance
boolean accepts = nfa.accepts("a");  // returns true
```

### Converting to DFA
```java
FSA dfa = nfa.toDFA();
boolean isDeterministic = dfa.deterministic();  // returns true
```

## Logical Corrections Made

1. **Epsilon closure includes state itself** - Fixed critical bug where ε-closure didn't include the state itself
2. **Removed duplicate state entries** - Eliminated redundant code that added states twice
3. **Simplified epsilon closure computation** - Removed unnecessary recomputation
4. **Fixed typos** - Corrected spelling errors in comments

## Testing
All tests pass successfully, validating:
- ✅ Correct epsilon closure computation
- ✅ Empty string acceptance
- ✅ NFA with epsilon transitions
- ✅ Determinism checking
- ✅ NFA to DFA conversion

## Security
✅ No security vulnerabilities detected (CodeQL analysis)

## References
This implementation follows standard automata theory principles as taught in formal language theory and compiler design courses.
