## このアプリケーションについて
必要なレシピ全体ををrecipe.yamlに記述し, 作成対象をアプリケーションに起動引数として与える事で標準出力(System.out)へ レシピツリー/必要リソース/副産物 が出力されます.

## レシピ記述について

```yaml:demo.yaml
Key :
  display:
  recipe:
    - []
    - ...
  result:
    - Key:
```
Key : `String`<br>
 そのレシピのキーですが, 主産物を指すIDのようにふるまいます.

display : `String`<br>
 -> 表示名です.

recipe : `List<Key[]>`<br>
 -> "材料とするキー"の配列 の リストが格納されます.

例えば:

```yaml
recipe:
  -  [A, B, C]
  -  [B, B, C]
  -  [B, B, C]
```

のように, クラフト・グリッドのような構造で記述できます.<br>
材料の個数はキーを記述した回数となるため, `[R, R, R, R]`は`R x4`という意味で解釈されますし, 3段で記述する必要もありません.

result : `List<Pair<Key, Long>>`<br>
 -> 結果のリストを, キー:数量 で記述します.

記述には一定のルールがあります.<br>
・displayがないとき: キーそのものがdisplayの値として扱われます.<br>
・recipe, resultがないとき: 一次リソース(採掘等で入手するべきリソース)として扱われます.


## 引数
-calc:[作成対象のkey]:[必要数(整数)]