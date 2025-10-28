# datamasking 🔒🧩

Lightweight data masking / anonymization utilities for development, testing, and safe data-sharing. datamasking helps you obfuscate or redact sensitive fields in datasets, logs, or streams while preserving structure and usability.

> ⚠️ This README is implementation-agnostic — adjust install & usage commands to match this repository's language and package layout (Python / Node / Go / etc.). Below each command section you'll find a copy-ready command box you can paste into your terminal.

---

## 🚀 Quick start — All common commands

Pick the section that matches your stack.

### Python 🐍

Copy these commands to create a virtualenv, install, and run tests:

```bash
# Create & activate virtualenv (macOS / Linux)
python -m venv .venv
source .venv/bin/activate

# Create & activate virtualenv (Windows PowerShell)
python -m venv .venv
.venv\\Scripts\\activate

# Install (editable/dev)
python -m pip install -e .

# Install release
python -m pip install .

# Install dev dependencies from requirements
pip install -r requirements-dev.txt

# Run tests
pytest

# Lint / format
ruff check .
flake8 .
black .

# Build sdist & wheel
python -m build

# Publish with twine (example)
python -m twine upload dist/*
```

---

### Node.js / JavaScript ⚡

Copy these commands to install dependencies, run tests, and build:

```bash
# Install dependencies
npm install

# Clean install in CI
npm ci

# Run tests
npm test

# Lint / format
npm run lint
npm run format

# Build
npm run build

# Run CLI locally via npx (if package has bin)
npx datamasking mask --help
```

---

### Go 🦫

Copy these commands to install, build, and test (if applicable):

```bash
# Install CLI (module aware)
go install github.com/nithin1734/datamasking/cmd/datamasking@latest

# Build locally
go build ./cmd/datamasking

# Run tests
go test ./...

# Run locally
./datamasking mask --input data/input.csv --output data/out.csv --config mask-rules.yml
```

---

### Docker 🐳

Copy these commands to build a Docker image and run the container:

```bash
# Build Docker image
docker build -t datamasking:latest .

# Run container (mount host data dir)
docker run --rm -v "$(pwd)"/data:/data datamasking:latest mask --input /data/input.csv --output /data/output.masked.csv --config /data/mask-rules.yml
```

---

## 📋 CLI examples (common flags) ✨

Use these paste-ready examples for common CLI tasks:

```bash
# Mask a CSV file
datamasking mask --input data/input.csv --output data/output.masked.csv --config mask-rules.yml

# Dry run (show changes without writing)
datamasking mask --input data/input.csv --dry-run --config mask-rules.yml

# Validate mask rules / config
datamasking validate --config mask-rules.yml

# Mask streaming JSON (stdin → stdout)
cat events.json | datamasking mask --format json --config mask-rules.yml > events.masked.json

# Deterministic hashing for IDs
datamasking mask --input data.csv --output out.csv --config mask-rules.yml --deterministic
```

---

## 🧭 Configuration example (YAML)

Example mask rules (mask-rules.yml):

```yaml
rules:
  - field: email
    strategy: redact
    redact_with: "[REDACTED]"
  - field: phone
    strategy: substitute
    substitute: "XXX-XXX-XXXX"
  - field: id
    strategy: hash
    deterministic: true
  - field: name
    strategy: pseudonymize
    preserve_length: true
```

Adjust the keys to match the actual config schema used by the project.

---

## 🧩 Library usage examples (pick one)

### Python (example)

```python
from datamasking import Masker

# load rules from YAML
masker = Masker.from_config("mask-rules.yml")

record = {"name": "Alice", "email": "alice@example.com", "id": "12345"}
masked = masker.mask_record(record)
print(masked)
```

### JavaScript (example)

```js
const { Masker } = require("datamasking");
const masker = Masker.fromConfig("mask-rules.json");

const record = { name: "Alice", email: "alice@example.com", id: "12345" };
const masked = masker.mask(record);
console.log(masked);
```

### Go (example)

```go
package main

import (
  "fmt"
  "github.com/nithin1734/datamasking/pkg/mask"
)

func main() {
  cfg, _ := mask.LoadConfig("mask-rules.yml")
  m := mask.NewMasker(cfg)
  rec := map[string]string{"name":"Alice","email":"alice@example.com"}
  out := m.MaskRecord(rec)
  fmt.Println(out)
}
```

(Replace package paths & API names to match the repo implementation.)

---

## ✅ Features (planned / implemented)
- Field-level masking (emails, names, phones, IDs) 🔐  
- Rule-based masking (regex or schema-driven) 📜  
- Deterministic masking (same input → same masked output) 🔁  
- In-place and copy masking modes 🔄  
- Strategies: redact, substitute, hash, pseudonymize 🛠️  
- CLI + library usage (scriptable & embeddable) 🧰

---

## 🧪 Testing & CI

Copy these suggestions for CI and local testing:

```bash
# Python
pytest

# Node
npm test

# Go
go test ./...

# Example: run tests & lint in a single script (bash)
pytest && npm test && go test ./...
```
Add CI config (GitHub Actions, GitLab CI, etc.) to run tests, lint, and build on push/PR.

---

## 🧾 Security & Data Handling
- Always work on copies of production data unless you have explicit permission. ⚠️  
- Document how deterministic masking works and whether it’s reversible. 🔍  
- Follow your organization's regulatory controls (GDPR, HIPAA, etc.). 🏛️

---

## 🤝 Contributing
- Fork the repo and create a feature branch  
- Add tests for new behavior or bug fixes  
- Run test & linter before creating a PR  
- Add CHANGELOG entry for breaking changes

Consider adding a CONTRIBUTING.md with repo-specific guidelines.

---

## 📜 License

This repository includes two license files so you can choose the licensing approach:
- LICENSE (MIT) — very permissive  
- LICENSE-APACHE-2.0 — permissive with an explicit patent grant

If you prefer a single license, remove the other file. If you want dual-licensing, keep both.

---

## ✨ Authors
- nithin1734 — repository owner

---

## 🙏 Acknowledgments
- List libraries, resources, or contributors that inspired this project.