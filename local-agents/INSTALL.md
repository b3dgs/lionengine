# Install a local agent machine from scratch (Windows 11)

For the owner, on each of the three PCs. Everything needed is on this page, in order. Copy each block as is. When you are done, go to [`README.md`](README.md) to run the benchmark.

Status: model names, file names, sizes and licenses were checked on Hugging Face on 2026-11-25, and the download links answer. **None of the commands has been run on a real Windows machine yet.** If one fails, report it: the page is then wrong, not you.

## What each PC needs

| | PC1 (RTX 5070 Ti, 16 GB) | PC2 (RTX 4070, 12 GB) | PC3 (RTX 2060, 8 GB) |
|---|---|---|---|
| Main model | Qwen3.6-35B-A3B Q4_K_M, 20.5 GB | Qwen3.6-35B-A3B Q4_K_M, 20.5 GB | Qwen3.5-9B Q4_K_M, 5.7 GB |
| Fallback model | gpt-oss-20b MXFP4, 12.1 GB | Qwen3-Coder-30B-A3B Q4_K_M, 18.6 GB | none |
| Mode | agent loop | agent loop | single shot |

Disk: the models of the column (PC1 32.6 GB, PC2 39.1 GB, PC3 5.7 GB), plus a few GB for the tools (more if you compile llama.cpp). All the models are Apache-2.0. llama.cpp is MIT. They are tools outside the build: nothing is added to the engine.

## 1. Check the machine

In PowerShell:

```powershell
winget --version        # if missing: install "App Installer" from the Microsoft Store
nvidia-smi              # must list the GPU; note the "CUDA Version" in the top right
```

If `nvidia-smi` fails or its CUDA version is lower than 13, update the NVIDIA driver from <https://www.nvidia.com/Download/index.aspx> and check again (the prebuilt llama.cpp of step 3 is a CUDA 13 build).

## 2. Install the tools

In PowerShell, then **close it and open a new one**:

```powershell
winget install --id Git.Git -e
winget install --id EclipseAdoptium.Temurin.25.JDK -e
winget install --id Python.Python.3.13 -e      # if the id is unknown: winget search Python.Python
winget install --id Anthropic.ClaudeCode -e    # or: irm https://claude.ai/install.ps1 | iex

# Maven 3.9 (no winget package): unzip to C:\tools and add it to your user PATH
curl.exe -L -o "$env:TEMP\maven.zip" https://archive.apache.org/dist/maven/maven-3/3.9.14/binaries/apache-maven-3.9.14-bin.zip
Expand-Archive "$env:TEMP\maven.zip" C:\tools -Force
$p = [Environment]::GetEnvironmentVariable('Path','User')
[Environment]::SetEnvironmentVariable('Path', "$p;C:\tools\apache-maven-3.9.14\bin", 'User')

# JAVA_HOME (the Temurin installer may not set it)
$jdk = (Get-ChildItem 'C:\Program Files\Eclipse Adoptium' -Directory | Where-Object Name -like 'jdk-25*' | Select-Object -First 1).FullName
[Environment]::SetEnvironmentVariable('JAVA_HOME', $jdk, 'User')

git config --global core.longpaths true
```

Open **Git Bash** (Start menu) and check:

```bash
git --version && java -version && mvn -v && python --version && claude --version
```

Five versions must print: Java 25 and Maven 3.9.x. Links if you prefer installers: [Git for Windows](https://git-scm.com/download/win), [Temurin 25](https://adoptium.net/installation/windows/), [Maven](https://maven.apache.org/download.cgi), [Python](https://www.python.org/downloads/windows/), [Claude Code setup](https://code.claude.com/docs/en/setup). Git for Windows must be installed: without it Claude Code falls back to a PowerShell tool, and the worker's command allow-list is written for its Bash tool (if Claude Code does not find Git Bash, set `CLAUDE_CODE_GIT_BASH_PATH` to its `bash.exe`, see the Claude Code setup page).

Get the repository, in Git Bash (keep the path short):

```bash
git clone https://github.com/b3dgs/lionengine /c/dev/lionengine
cd /c/dev/lionengine && git checkout rewrite/v11
```

## 3. Install llama.cpp (prebuilt, nothing to compile)

1. Open <https://github.com/ggml-org/llama.cpp/releases/latest>.
2. In the assets, download the **Windows x64 CUDA 13** zip, `llama-<tag>-bin-win-cuda-13.x-x64.zip`, and, if the release lists it, the matching `cudart-llama-bin-win-cuda-13.x-x64.zip`.
3. Extract both into the same folder, `C:\llm\llama.cpp`.
4. Check, in PowerShell:

```powershell
C:\llm\llama.cpp\llama-server.exe --version
C:\llm\llama.cpp\llama-server.exe --list-devices     # must list your NVIDIA GPU (see --help if your build names it differently)
```

Use the **CUDA 13** build on all three PCs: the CUDA 12.4 builds are reported not to work on RTX 50 cards such as PC1's, and the CUDA 13 builds are reported to cover the RTX 20 to 50 series (PC3, PC2, PC1). If the release page offers other CUDA versions, choose the newest 13.

### Only if the prebuilt does not work: compile it

Install the compiler tools **in this order** (the CUDA toolkit installs its Visual Studio integration only if Visual Studio is already there):

```powershell
winget install --id Kitware.CMake -e
winget install --id Microsoft.VisualStudio.2022.BuildTools -e --override "--wait --passive --add Microsoft.VisualStudio.Workload.VCTools --includeRecommended"
winget install --id Nvidia.CUDA -e
```

Then, in "x64 Native Tools Command Prompt for VS 2022" from the Start menu:

```bat
git clone https://github.com/ggml-org/llama.cpp C:\llm\llama.cpp-src
cd C:\llm\llama.cpp-src
cmake -B build -DGGML_CUDA=ON
cmake --build build --config Release -j
```

The programs are in `C:\llm\llama.cpp-src\build\bin\Release`: use that folder instead of `C:\llm\llama.cpp` below. If the configure step says an architecture is unsupported on the RTX 5070 Ti, add `-DCMAKE_CUDA_ARCHITECTURES=120` to the first `cmake` line. If a CUDA toolkit produces garbage output, try another toolkit version. Reference: [llama.cpp build guide](https://github.com/ggml-org/llama.cpp/blob/master/docs/build.md).

## 4. Download the models

In PowerShell. Take only the lines of your PC; `-C -` resumes an interrupted download, and no account is needed.

```powershell
mkdir C:\llm\models -Force; cd C:\llm\models

# PC1 and PC2: main model (20.5 GB)
curl.exe -L -C - -O https://huggingface.co/ggml-org/Qwen3.6-35B-A3B-GGUF/resolve/main/Qwen3.6-35B-A3B-Q4_K_M.gguf
# PC1: fallback (12.1 GB)
curl.exe -L -C - -O https://huggingface.co/ggml-org/gpt-oss-20b-GGUF/resolve/main/gpt-oss-20b-MXFP4.gguf
# PC2: fallback (18.6 GB)
curl.exe -L -C - -O https://huggingface.co/unsloth/Qwen3-Coder-30B-A3B-Instruct-GGUF/resolve/main/Qwen3-Coder-30B-A3B-Instruct-Q4_K_M.gguf
# PC3: main model (5.7 GB)
curl.exe -L -C - -O https://huggingface.co/unsloth/Qwen3.5-9B-GGUF/resolve/main/Qwen3.5-9B-Q4_K_M.gguf
```

Model pages: [Qwen3.6-35B-A3B](https://huggingface.co/ggml-org/Qwen3.6-35B-A3B-GGUF), [gpt-oss-20b](https://huggingface.co/ggml-org/gpt-oss-20b-GGUF), [Qwen3-Coder-30B-A3B](https://huggingface.co/unsloth/Qwen3-Coder-30B-A3B-Instruct-GGUF), [Qwen3.5-9B](https://huggingface.co/unsloth/Qwen3.5-9B-GGUF).

## 5. Start the model server

In its own PowerShell window, left open while you work. `--jinja` is required for tool calls. `--alias` is the model name you give to the scripts in step 6.

```powershell
# PC1 and PC2: main model. --n-cpu-moe N keeps the expert layers of the first N layers in RAM.
C:\llm\llama.cpp\llama-server.exe -m C:\llm\models\Qwen3.6-35B-A3B-Q4_K_M.gguf --alias qwen3.6-35b-a3b --jinja -c 65536 -ngl 99 --n-cpu-moe 99 --host 127.0.0.1 --port 8080

# PC1 fallback
C:\llm\llama.cpp\llama-server.exe -m C:\llm\models\gpt-oss-20b-MXFP4.gguf --alias gpt-oss-20b --jinja -c 65536 -ngl 99 --host 127.0.0.1 --port 8080

# PC2 fallback
C:\llm\llama.cpp\llama-server.exe -m C:\llm\models\Qwen3-Coder-30B-A3B-Instruct-Q4_K_M.gguf --alias qwen3-coder-30b-a3b --jinja -c 65536 -ngl 99 --n-cpu-moe 99 --host 127.0.0.1 --port 8080

# PC3
C:\llm\llama.cpp\llama-server.exe -m C:\llm\models\Qwen3.5-9B-Q4_K_M.gguf --alias qwen3.5-9b --jinja -c 16384 -ngl 99 --host 127.0.0.1 --port 8080
```

Tuning, because the right numbers depend on the machine and were not measured:

- **`--n-cpu-moe 99` is the safe start** (all expert layers in RAM: it loads, but slowly). Lower it step by step (for example 40, 30, 20) while `nvidia-smi` shows VRAM not yet full; raise it if the server runs out of memory.
- If the model does not load or runs out of memory, lower `-c` (for example 32768).
- Check the flags of your build with `llama-server.exe --help`.
- Models are loaded from the files, so no cache folder or account is involved.

Check that it answers, in Git Bash:

```bash
curl -s http://127.0.0.1:8080/health          # {"status":"ok"} once the model is loaded
```

## 6. Point the scripts at it and test

In Git Bash, in the repository:

```bash
cd /c/dev/lionengine
export ANTHROPIC_BASE_URL=http://127.0.0.1:8080
export LOCAL_AGENT_MODEL=qwen3.6-35b-a3b      # the --alias of step 5
ANTHROPIC_API_KEY=local-gateway claude -p --bare --tools "" --model "$LOCAL_AGENT_MODEL" "Reply with the word ok"
bash local-agents/run-benchmark.sh --dry-run pc1 1
```

The first command must print an answer from the local model (the first request can take minutes on a large model). The dry run prints the plan and changes nothing. Then follow [`README.md`](README.md), section "Per run".

If the Claude Code command fails, the gateway is not an Anthropic-compatible server or was started without `--jinja`: see the troubleshooting table of [`README.md`](README.md).
