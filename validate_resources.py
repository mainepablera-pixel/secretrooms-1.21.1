"""Static JSON/resource checks for the Secret Rooms NeoForge 1.21.1 port.
Run from the repository root with: python validate_resources.py
This does not compile Java or validate Minecraft runtime behavior.
"""
import json
from pathlib import Path

root = Path("src/main/resources")
json_files = list(root.rglob("*.json"))
errors = []
for path in json_files:
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except Exception as exc:
        errors.append(f"Invalid JSON {path}: {exc}")
        continue
    if "recipe" in path.parts and "data" in path.parts and path.name.endswith(".json"):
        if isinstance(data, dict) and "result" in data and isinstance(data["result"], dict):
            if "id" not in data["result"]:
                errors.append(f"Recipe result missing 1.21.1 result.id: {path}")

print(f"Parsed {len(json_files)} JSON files")
if errors:
    print("Validation errors:")
    for error in errors:
        print(f"- {error}")
    raise SystemExit(1)
print("JSON syntax and recipe result checks passed")
print("Note: this does not replace a Gradle build or Minecraft runtime tests.")
