# GTFStoOuDia
GTFS形式の列車・バス形式の時刻表をOuDiaファイル形式に変換します

## 使用方法
main関数は「src/GTFStoOuDia.java」にあります

### ソースコードを書き換える場合
+ 「src/GTFStoOuDia.java」の「GTFSdirectoryPath」にGTFSのディレクトリを指定します。
 多くの場合、GTFSはzip形式で配布されます。zip形式を展開し、「GTFSdirectoryPath」直下に「route.txt」などGTFSを構成するファイルが存在するようにしてください。
+ 「src/GTFStoOuDia.java」の「convertInfoFilePath」に「変換に必要な情報が書かれたファイル」のパスを入力してください。
　「変換に必要な情報が書かれたファイル」のサンプルはルートフォルダにある「input.txt」です。フォーマットの詳細は「src/GTFStoOuDia.java」の該当部分を参考にしてください。
### 引数を用いる場合
「src/GTFStoOuDia.java」の「loadFromArgs」をtrueに変更し、
第一引数に「GTFSdirectoryPath」、第二引数に「convertInfoFilePath」を指定してください。

## LICENSE
+ Copyright(c) 2019 KameLong
+ contact:kamelong.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program(COPYING.txt).  If not, see <http://www.gnu.org/licenses/>.

このプログラムはGNU GPL version 3(もしくはそれ以上のバージョン)ライセンスに従って配布しています。
このプログラムは製作者の許可を取ることなく、誰でも自由に複製・改変・頒布することが許可されています。
ただし、このプログラムを使用した制作物はGNU-GPLライセンスで配布しなければなりません。
