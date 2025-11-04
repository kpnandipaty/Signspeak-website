import java.util.*;

public class FSATest {
    public static void main(String[] args) {
        System.out.println("=== FSA Logic Error Fixes Test ===\n");
        
        // Test 1: Epsilon closure should include the state itself
        System.out.println("Test 1: Epsilon Closure Includes State Itself");
        FSA nfa1 = new FSA();
        int s0 = nfa1.addState(true, true);  // initial and accepting
        int s1 = nfa1.addState(false, false);
        nfa1.addTransition(s0, "a", s1);
        
        ArrayList<Integer> closure0 = nfa1.closure(s0);
        System.out.println("Closure of state 0: " + closure0);
        System.out.println("Expected: [0] - " + (closure0.contains(0) && closure0.size() == 1 ? "PASS" : "FAIL"));
        System.out.println();
        
        // Test 2: Accept empty string on accepting initial state
        System.out.println("Test 2: Accept Empty String on Accepting Initial State");
        boolean acceptsEmpty = nfa1.accepts("");
        System.out.println("Accepts empty string: " + acceptsEmpty);
        System.out.println("Expected: true - " + (acceptsEmpty ? "PASS" : "FAIL"));
        System.out.println();
        
        // Test 3: NFA with epsilon transitions
        System.out.println("Test 3: NFA with Epsilon Transitions");
        FSA nfa2 = new FSA();
        int q0 = nfa2.addState(true, false);  // initial
        int q1 = nfa2.addState(false, false);
        int q2 = nfa2.addState(false, true);   // accepting
        nfa2.addTransition(q0, "", q1);        // epsilon transition
        nfa2.addTransition(q1, "a", q2);
        
        ArrayList<Integer> closureQ0 = nfa2.closure(q0);
        System.out.println("Closure of state 0: " + closureQ0);
        System.out.println("Expected: [0, 1] - " + (closureQ0.contains(0) && closureQ0.contains(1) && closureQ0.size() == 2 ? "PASS" : "FAIL"));
        
        boolean acceptsA = nfa2.accepts("a");
        System.out.println("Accepts 'a': " + acceptsA);
        System.out.println("Expected: true - " + (acceptsA ? "PASS" : "FAIL"));
        System.out.println();
        
        // Test 4: Check deterministic (should be false for nfa2)
        System.out.println("Test 4: Determinism Check");
        boolean isDet = nfa2.deterministic();
        System.out.println("NFA with epsilon is deterministic: " + isDet);
        System.out.println("Expected: false - " + (!isDet ? "PASS" : "FAIL"));
        System.out.println();
        
        // Test 5: Convert to DFA
        System.out.println("Test 5: NFA to DFA Conversion");
        FSA dfa = nfa2.toDFA();
        boolean dfaAcceptsA = dfa.accepts("a");
        boolean dfaIsDet = dfa.deterministic();
        System.out.println("DFA accepts 'a': " + dfaAcceptsA);
        System.out.println("DFA is deterministic: " + dfaIsDet);
        System.out.println("Expected: both true - " + (dfaAcceptsA && dfaIsDet ? "PASS" : "FAIL"));
        System.out.println();
        
        System.out.println("=== All Tests Complete ===");
    }
}
