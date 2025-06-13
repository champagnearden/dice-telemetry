clear
unset JAVA_TOOL_OPTIONS
PROJECT_NAME="service"
JAR_PATH="build/libs/${PROJECT_NAME}.jar"
SHA_FILE="${PROJECT_NAME}.sha512"

echo "Comparing hashes..."
current_sha=$( \
  find "service" -type f \
  -exec sha512sum {} + \
  | sort \
  | sha512sum \
  | awk '{print $1}' \
)
# Read stored SHA-1
read -r stored_sha stored_file < "service/${SHA_FILE}"
cd service
if [[ "${current_sha}" != "${stored_sha}" ]]; then
  echo "JAR changed (old: ${stored_sha}, new: ${current_sha}); rebuilding…"
  ./gradlew assemble || exit 1
  # Update the stored SHA-512
  echo "${current_sha}  ${JAR_PATH}" > "${SHA_FILE}"
else
  echo "No changes detected; skipping build."
fi

echo "Launching app"
java -jar "${JAR_PATH}"

