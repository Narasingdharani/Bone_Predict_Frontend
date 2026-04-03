import sys

def check_braces(filename):
    try:
        with open(filename, 'r', encoding='utf-8') as f:
            content = f.read()
        
        count = 0
        stack = []
        for i, char in enumerate(content):
            if char == '{':
                count += 1
                stack.append(i)
            elif char == '}':
                count -= 1
                if stack:
                    stack.pop()
                else:
                    print(f"Extra closing brace at char {i}")
        
        if count > 0:
            print(f"MISSING_BRACES: {count} closing braces. Last open brace at char {stack[-1]}")
            # Let's find the line number of the last open brace
            line_no = content.count('\n', 0, stack[-1]) + 1
            print(f"LINE_OF_LAST_OPEN: {line_no}")
        elif count < 0:
            print(f"EXTRA_BRACES: {-count} extra closing braces.")
        else:
            print("Braces are BALANCED.")
    except Exception as e:
        print(f"ERROR: {str(e)}")

if __name__ == "__main__":
    check_braces(sys.argv[1])
