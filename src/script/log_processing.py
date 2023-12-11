import sys
import os

def process_log_file(log_file_path):
    total_execution_times = []
    jdbc_execution_times = []

    with open(log_file_path, 'r') as file:
        for line in file:
            if "Search servlet total execution time" in line:
                total_execution_time_ns = int(line.split(":")[1].strip().split()[0])
                total_execution_time_ms = total_execution_time_ns / 1e6  # Convert nanoseconds to milliseconds
                total_execution_times.append(total_execution_time_ms)
            elif "JDBC execution time" in line:
                jdbc_execution_time_ns = int(line.split(":")[1].strip().split()[0])
                jdbc_execution_time_ms = jdbc_execution_time_ns / 1e6  # Convert nanoseconds to milliseconds
                jdbc_execution_times.append(jdbc_execution_time_ms)

    # Calculate averages
    avg_total_execution_time = sum(total_execution_times) / len(total_execution_times)
    avg_jdbc_execution_time = sum(jdbc_execution_times) / len(jdbc_execution_times)

    print("Average Total Execution Time (TS):", avg_total_execution_time, "ms")
    print("Average JDBC Execution Time (TJ):", avg_jdbc_execution_time, "ms")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <log_file_name>")
    else:
        script_directory = os.path.dirname(os.path.abspath(__file__))
        log_file_name = sys.argv[1]
        log_file_path = os.path.join(script_directory, log_file_name)
        
        print("Processing log file:", log_file_path)
        
        if not os.path.isfile(log_file_path):
            print(f"Error: File '{log_file_path}' not found.")
        else:
            process_log_file(log_file_path)
