# datamasking 🔒🧩
Datamasking is a small utility/library for masking or redacting sensitive data in logs, files, and API responses. It helps teams keep personally identifiable information (PII) and other secrets safe by replacing or obfuscating sensitive fields before storing or transmitting data.

## About

- Purpose: Protect sensitive information by applying configurable masking rules to structured and unstructured data.
- Use cases: Logging pipelines, API response filtering, test data generation, and data export sanitization.
- Goal: Provide an easy-to-integrate, fast, and configurable solution for teams that need to comply with privacy requirements or reduce risk from exposed data.

> ⚠️ This README is implementation-agnostic — adjust install & usage commands to match this repository's language and package layout (Python / Node / Go / etc.). Below each command section you'll find a copy-ready command box you can paste into your terminal.

---

## 🚀 Quick start — All common commands

Pick the section that matches your stack.

## Installation (single-command)

To keep installation simple, this project supports a single setup script that installs Python dependencies from requirements.txt and Node dependencies (if package.json exists).

1. Make sure you have Python 3 and/or Node.js installed.
2. Create a requirements.txt file at the project root (see example below) or generate one from your environment.
3. Run the single command:

```bash
# Unix / macOS
./setup.sh

# Windows (PowerShell)
.\setup.ps1
```

What the script does:
- Creates a Python virtual environment at .venv (if not present)
- Installs Python packages from requirements.txt into the venv
- Runs npm ci if package.json is present
- Prints final instructions to activate the venv

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

## 🙏 Acknowledgments
- List libraries, resources, or contributors that inspired this project.
