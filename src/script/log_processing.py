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

# Usage
log_file_path = "/Users/kashmoney/gitclones/2023-fall-cs122b-bobaholic/src/logs/logfile.txt"
process_log_file(log_file_path)
