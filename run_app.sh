export DICE_IMAGE_TAG=$(date +%Y%m%d-%H%M)
alias python=python3
alias pip=pip3
# --- Function to clean up on exit ---
cleanup() {
  echo -e "\nCtrl+C detected! Shutting down..."

  # 1. Shutdown Docker Compose
  cd ../docker
  echo "Stopping Docker Compose services..."
  docker compose down --remove-orphans --volumes

  # 2. Shutdown Python script
  if [ -n "$PYTHON_PID" ]; then # Check if PID exists
    echo "Stopping Python script (PID: $PYTHON_PID)..."
    kill "$PYTHON_PID"
    wait "$PYTHON_PID" 2>/dev/null # Wait for it to terminate, suppress "No such process" if already dead
    echo "Python script stopped."
  else
    echo "Python script not running or PID not found."
  fi

  echo "Cleanup complete. Exiting."
  exit 0 # Exit cleanly
}

# --- Trap SIGINT (Ctrl+C) to call the cleanup function ---
trap cleanup SIGINT

# --- Main execution ---

echo "Starting Python script in background..."

# Launch Python script in background
# Using 'exec' here so the shell doesn't create an extra process for python,
# and it helps with signal handling slightly.
# and redirect its output.
cd test_service
pip install -r requirements.txt
python generate_trafic.py 5 > /dev/null & # Launch the script in background and capture its output
PYTHON_PID=$! # Capture the PID of the last background command
cd ..

if [ -z "$PYTHON_PID" ] || ! ps -p "$PYTHON_PID" > /dev/null; then
    echo "Error: Failed to launch Python script. Exiting."
    cleanup # Attempt cleanup even on failed launch
    exit 1
fi

echo "Python script launched with PID: $PYTHON_PID"
echo "Output won't go to the terminal unless redirected in your script. (l 40)"

echo "Launching Docker Compose..."
# --abort-on-container-exit will stop all services if any one of them exits.
# This command runs in the foreground, so the script will wait here until
# docker compose exits or is interrupted.
cd docker
docker compose build --no-cache dice-telemetry
docker compose up

# If docker compose exits normally (e.g., all services stop), cleanup should still run
echo "Docker Compose exited normally."
cleanup
