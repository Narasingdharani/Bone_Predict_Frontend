import sys

def trace_nesting(filename):
    with open(filename, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    level = 0
    for i, line in enumerate(lines):
        for char in line:
            if char == '{':
                level += 1
            elif char == '}':
                level -= 1
        
        if level < 0:
            print(f"ERROR: Negative nesting at line {i+1}")
            level = 0
    
    if level > 0:
        print(f"ERROR: {level} unclosed braces at end of file.")

if __name__ == "__main__":
    trace_nesting(sys.argv[1])
