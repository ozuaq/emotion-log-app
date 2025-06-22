#!/bin/bash
set -e # コマンドがエラーで終了したら直ちにスクリプトを終了する

echo "Updating package lists and installing base dependencies..."
sudo apt-get update && sudo apt-get install -y tree unzip zip curl sqlite3

echo "sdkman and sbt installation..."
curl -s "https://get.sdkman.io" | bash
source /usr/local/sdkman/bin/sdkman-init.sh
sdk install sbt

echo "Post-create script finished successfully."
