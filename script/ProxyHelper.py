import os
import io
import subprocess
import sys
from pathlib import Path
import json

def file_insert_line(path, line, text):
    data = file_read(path)
    data[line] = text + '\n' + data[line]
    try:
        with open('stats.txt', 'w') as file:
            file.writelines(data)
    except IOError:
            file.close()
    finally:
            file.close()

def file_read(path):
    with open(path, 'r') as file:
        return file.readlines()

def process_code(workspace, command, output):
    process = subprocess.Popen(command, cwd = workspace,shell = True, bufsize = 1, stdout=subprocess.PIPE, stderr = subprocess.STDOUT, encoding = 'utf-8', errors = 'replace' )
    if output:
        while True:
            realtime_output = process.stdout.readline()
            if realtime_output == '' and process.poll() is not None:
                break
            if realtime_output:
                print(realtime_output.strip(), flush=False)
                sys.stdout.flush()
    return process.returncode


def process_stdout(workspace, command):
    process = subprocess.Popen(command, cwd=workspace, stdout=subprocess.PIPE)
    return process.stdout

workspace = str(Path(os.getcwd()).parent.resolve())
fileName = workspace + "/src/main/java/extra-proxy-config.json"
exception = 'Caused by: com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces ';
print(str(fileName))
code, count = 0, 0
found = True

while code == 0 and found:

    found = False

    code = process_code(workspace, "mvn -DskipTests clean package -Pnative", True)

    if code == 0:
        stdout = process_stdout(workspace, "target/cobnet")
        while True:
            line = stdout.readline().decode("utf-8")
            if line == None or len(line) == 0:
                break
            if line.startswith(exception):
                found = True
                variable = "  ,\n  {\n    \"interfaces\" :[" + ",".join(map(lambda node: " \"" + node[10 : len(node)].strip() + "\"", line[line.index('[') + 1 : line.index(']')].split(","))) + "] }"
                file_insert_line(fileName, len(list(filter(lambda text : text != None and len(text) != 0, file_read(fileName)))) - 1, variable)
                print(variable)
                count += 1
                break
print("auto fill proxy end with code: {0}, found {1} proxies.%n".format(code, count))
