#!/bin/bash
set -e # コマンドがエラーで終了したら直ちにスクリプトを終了する

echo "Updating package lists and installing base dependencies..."
sudo apt-get update && sudo apt-get install -y unzip zip curl sqlite3

echo "Installing sbt..."
SBT_VERSION="1.9.9"
SBT_TGZ_URL="https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz"
SBT_TEMP_FILE="/tmp/sbt-${SBT_VERSION}.tgz"
# インストール先のディレクトリと実行ファイルのパスを定義
SBT_INSTALL_PARENT_DIR="/usr/local"
SBT_EXECUTABLE_PATH="${SBT_INSTALL_PARENT_DIR}/sbt/bin/sbt"
SBT_LINK_TARGET_DIR="/usr/local/bin"

# sbtのダウンロード
echo "Downloading sbt from ${SBT_TGZ_URL} to ${SBT_TEMP_FILE}..."
# -L: リダイレクトを追跡
# -f: HTTPエラー時にサイレントに失敗 (スクリプトのため、エラーは明示的にチェック)
# -sS: サイレントモードだがエラーは表示
curl -LfsS -o "${SBT_TEMP_FILE}" "${SBT_TGZ_URL}"

# ダウンロードが成功し、ファイルが空でないか確認
if [ $? -ne 0 ] || [ ! -s "${SBT_TEMP_FILE}" ]; then
    echo "Error: Failed to download sbt from ${SBT_TGZ_URL} or the downloaded file is empty."
    # 一時ファイルが存在する場合は削除
    if [ -f "${SBT_TEMP_FILE}" ]; then
        rm "${SBT_TEMP_FILE}"
    fi
    exit 1
fi
echo "sbt downloaded successfully."

echo "Extracting sbt to ${SBT_INSTALL_PARENT_DIR}..."
# tar.gz ファイルを展開すると、通常 'sbt' という名前のディレクトリが作成される
# 例: /usr/local/sbt/
if sudo tar xzf "${SBT_TEMP_FILE}" -C "${SBT_INSTALL_PARENT_DIR}"; then
    echo "sbt extracted successfully."
else
    echo "Error: Failed to extract sbt."
    rm "${SBT_TEMP_FILE}" # 一時ファイルをクリーンアップ
    exit 1
fi

# sbt実行ファイルが存在するか確認
if [ ! -f "${SBT_EXECUTABLE_PATH}" ]; then
    echo "Error: sbt executable not found at ${SBT_EXECUTABLE_PATH} after extraction."
    echo "Debug information:"
    echo "Contents of ${SBT_INSTALL_PARENT_DIR}:"
    sudo ls -l "${SBT_INSTALL_PARENT_DIR}"
    if [ -d "${SBT_INSTALL_PARENT_DIR}/sbt" ]; then
        echo "Contents of ${SBT_INSTALL_PARENT_DIR}/sbt:"
        sudo ls -l "${SBT_INSTALL_PARENT_DIR}/sbt"
        if [ -d "${SBT_INSTALL_PARENT_DIR}/sbt/bin" ]; then
            echo "Contents of ${SBT_INSTALL_PARENT_DIR}/sbt/bin:"
            sudo ls -l "${SBT_INSTALL_PARENT_DIR}/sbt/bin"
        fi
    fi
    rm "${SBT_TEMP_FILE}" # 一時ファイルをクリーンアップ
    exit 1
fi

echo "Creating symlink for sbt at ${SBT_LINK_TARGET_DIR}/sbt"
sudo ln -sf "${SBT_EXECUTABLE_PATH}" "${SBT_LINK_TARGET_DIR}/sbt"

echo "sbt installed successfully."
sbt -version # sbtのインストールを検証

echo "Cleaning up temporary sbt download..."
rm "${SBT_TEMP_FILE}"

echo "Installing Angular CLI..."
# Node.js feature でインストールした場合、グローバルインストールに sudo は不要なことが多い
if npm install -g @angular/cli; then
    echo "Angular CLI installed successfully."
else
    echo "Failed to install Angular CLI without sudo. Trying with sudo..."
    sudo npm install -g @angular/cli
    echo "Angular CLI installed with sudo."
fi

ng version # Angular CLI のインストールを検証

echo "Post-create script finished successfully."
