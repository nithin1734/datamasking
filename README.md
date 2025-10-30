# datamasking 🔒🧩
Datamasking is a small utility/library for masking or redacting sensitive data in logs, files, and API responses. It helps teams keep personally identifiable information (PII) and other secrets safe by replacing or obfuscating sensitive fields before storing or transmitting data.

## About

- Purpose: Protect sensitive information by applying configurable masking rules to structured and unstructured data.
- Use cases: Logging pipelines, API response filtering, test data generation, and data export sanitization.
- Goal: Provide an easy-to-integrate, fast, and configurable solution for teams that need to comply with privacy requirements or reduce risk from exposed data.

> ⚠️ This README is implementation-agnostic — adjust install & usage commands to match this repository's language and package layout (Python / Node / Go / etc.). Below each command section you'll find a copy-ready command box you can paste into your terminal.

---

## Quick start — Single-command setup

Use the included setup scripts to create a Python virtual environment and install runtime + dev dependencies, and (if present) install Node dependencies.

Unix / macOS (one-liner):
```bash
# Make setup script executable and run it
chmod +x ./setup.sh
./setup.sh

# OR do everything inline (single command)
python3 -m venv .venv && source .venv/bin/activate && python -m pip install --upgrade pip && pip install -r requirements.txt -r requirements-dev.txt
```

Windows (PowerShell):
```powershell
# Run the PowerShell setup script
.\setup.ps1

# OR inline:
python -m venv .venv; .\.venv\Scripts\Activate.ps1; python -m pip install --upgrade pip; pip install -r requirements.txt -r requirements-dev.txt
```

What the setup scripts do:
- Create a virtualenv at .venv (if missing)
- Upgrade pip in the venv
- Install runtime packages from requirements.txt
- Install development packages from requirements-dev.txt
- If package.json exists, run npm ci to install Node dependencies

Activate the venv after setup:
- Unix / macOS: source .venv/bin/activate
- Windows PowerShell: .\.venv\Scripts\Activate.ps1

---

## Files: requirements & setup scripts

- requirements.txt — runtime dependencies (kept minimal; enable optional packages as needed)
- requirements-dev.txt — development & CI tools (linters, test runners, packaging)
- setup.sh — cross-platform-ish shell script to automate venv creation and installs
- setup.ps1 — PowerShell script for Windows automation

Example (already included in repo):
```text
# requirements.txt
pyyaml>=6.0
click>=8.1.3
requests>=2.31.0
# optional:
# regex>=2023.8.8
# cryptography>=41.0.0
```

```text
# requirements-dev.txt
pytest>=7.3.0
ruff>=0.17.0
black>=24.0.0
flake8>=6.0.0
mypy>=1.4.0
build>=1.0.0
twine>=4.0.0
isort>=5.12.0
```

---

## Python — Install & run (recommended for library/CLI dev)

1. Create & activate venv:
```bash
python3 -m venv .venv
source .venv/bin/activate   # Unix / macOS
# or on Windows PowerShell:
# .\.venv\Scripts\Activate.ps1
```

2. Install dependencies:
```bash
pip install --upgrade pip
pip install -r requirements.txt           # runtime
pip install -r requirements-dev.txt       # dev (optional)
```

3. Install the package (editable, for development):
```bash
pip install -e .
```

4. Run tests / linters:
```bash
pytest
ruff check .
flake8 .
black .
mypy
```

5. Build / publish:
```bash
python -m build
python -m twine upload dist/*
```

Run the CLI (after pip install -e . or pip install .):
```bash
# Mask a CSV
datamasking mask --input data/input.csv --output data/output.masked.csv --config mask-rules.yml

# Dry run
datamasking mask --input data/input.csv --dry-run --config mask-rules.yml

# Validate rules
datamasking validate --config mask-rules.yml
```

If the CLI executable is not installed, run via module (if available):
```bash
python -m datamasking mask --input data/input.csv --config mask-rules.yml
```

---

## Node.js / JavaScript (if applicable)

Install & run Node tasks:
```bash
# Install dependencies
npm install

# Clean CI install
npm ci

# Run tests
npm test

# Lint / format / build
npm run lint
npm run format
npm run build

# Run CLI locally (without global install)
npx datamasking mask --help
```

---

## Go (if the Go CLI is included)

Build & run:
```bash
# Install CLI (module-aware)
go install github.com/nithin1734/datamasking/cmd/datamasking@latest

# Or build locally
go build ./cmd/datamasking

# Run tests
go test ./...

# Run CLI
./datamasking mask --input data/input.csv --output data/output.masked.csv --config mask-rules.yml
```

---

## Docker

Build and run using Docker:
```bash
# Build Docker image
docker build -t datamasking:latest .

# Run (example mounts a host data directory)
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
## Development environment & tools

A generic list of software commonly used to prepare and maintain this project:

- Version control and hosting
  - Git
  - GitHub

- Languages & runtimes
  - Node.js (JavaScript / TypeScript)
  - Python
  - Go (if applicable)

- Package managers & dependency tools
  - npm / yarn (Node)
  - pip / pipenv / Poetry (Python)
  - go modules (go.mod)

- Build, task runners & packaging
  - npm scripts / Make
  - webpack / rollup (for bundling JavaScript)
  - setup.py / setuptools (Python packaging)

- Containerization & deployment
  - Docker (Dockerfile, docker-compose)
  - Kubernetes / Helm (optional for deployment)

- Continuous integration / delivery
  - GitHub Actions
  - Travis CI / CircleCI (optional)

- Testing, linting & formatting
  - Jest / Mocha (JavaScript)
  - pytest (Python)
  - ESLint, Prettier (code quality / formatting)

- Documentation & docs tooling
  - README.md (Markdown)
  - JSDoc / TypeDoc (JS docs) or Sphinx (Python docs)

- Security & dependency scanning
  - Dependabot / npm audit / safety

- Editors & IDEs (commonly used)
  - Visual Studio Code
  - IntelliJ / PyCharm (as applicable)

- Utilities & libraries often used
  - Regex libraries (built-in)
  - Hashing/encryption libraries (crypto, hashlib, bcrypt)
  - Logging libraries (Winston, loguru, logback)
    
## 📜 License

This repository includes two license files so you can choose the licensing approach:
- LICENSE (MIT) — very permissive  
- LICENSE-APACHE-2.0 — permissive with an explicit patent grant

If you prefer a single license, remove the other file. If you want dual-licensing, keep both.

---

## ✨ Authors
- nithin1734 — repository owner

---


