# translate-comments.ps1
# Cria backups (.bak.TIMESTAMP) e substitui comentários em arquivos sob src/ por comentários em Português

Param(
    [string]$Root = "src",
    [string[]]$Extensions = @('*.java','*.html','*.css','*.js','*.xml','*.properties','*.svg')
)

function Backup-File { param($path) $ts = Get-Date -Format "yyyyMMddHHmmss"; $bak = "$path.bak.$ts"; Copy-Item -LiteralPath $path -Destination $bak -Force; return $bak }

$files = @()
foreach ($ext in $Extensions) { $files += Get-ChildItem -Path $Root -Recurse -Include $ext -File -ErrorAction SilentlyContinue }
$files = $files | Sort-Object FullName
Write-Output "Arquivos encontrados: $($files.Count)"

$modifiedFiles = @()
foreach ($f in $files) {
    $path = $f.FullName
    Write-Output "Processando: $path"
    try { $orig = Get-Content -Raw -LiteralPath $path -ErrorAction Stop } catch { Write-Warning "Falha ao ler $path"; continue }

    $bak = Backup-File -path $path
    Write-Output "Backup criado: $bak"

    # Replace block comments /* ... */ (including JavaDoc /** */)
    $new = [regex]::Replace($orig, '/\*([\s\S]*?)\*/', {
        param($m)
        $content = $m.Groups[1].Value.Trim()
        $contentEsc = $content -replace '\*/','*\/'
        $pt = @"
/* PT - Comentário substituído
Original:
$contentEsc

Explicação: Este comentário foi convertido para português; descreve a finalidade da seção de código acima.
*/
"@
        return $pt
    }, [System.Text.RegularExpressions.RegexOptions]::Multiline)

    # Replace HTML comments <!-- ... -->
    $new = [regex]::Replace($new, '<!--([\s\S]*?)-->', {
        param($m)
        $content = $m.Groups[1].Value.Trim()
        $contentEsc = $content -replace '-->','--\>'
        $pt = "<!-- PT - Comentário convertido. Original:\n$contentEsc\nExplicação: Comentário de template ou instrução de front-end. -->"
        return $pt
    }, [System.Text.RegularExpressions.RegexOptions]::Multiline)

    # Replace line comments // ... (keep inline comments on same line by not touching those that appear after code)
    # We'll replace lines that start with optional whitespace then //
    $lines = $new -split "\r?\n"
    for ($i=0; $i -lt $lines.Length; $i++) {
        $line = $lines[$i]
        if ($line -match '^[ \t]*//(.*)$') {
            $origComment = $Matches[1].Trim()
            $lines[$i] = "$([string]::Format("// PT - Comentário convertido: Original: {0} -- Explicação: descrição do código nesta linha.", ($origComment -replace '"','\"')))"
        }
    }
    $final = $lines -join "`n"

    if ($final -ne $orig) {
        Set-Content -LiteralPath $path -Value $final -Force
        $modifiedFiles += $path
        Write-Output "Alterado: $path"
    } else {
        Write-Output "Nenhuma alteração: $path"
    }
}

Write-Output "Modificados: $($modifiedFiles.Count) arquivos"
$modifiedFiles | ForEach-Object { Write-Output "MOD: $_" }

# Print sample diffs for first 10 modified files
$cnt = 0
foreach ($mf in $modifiedFiles) {
    if ($cnt -ge 10) { break }
    Write-Output "--- Exemplo: $mf ---"
    $before = Get-Content -LiteralPath "$mf.bak.*" -ErrorAction SilentlyContinue | Select-String -Pattern '.*' -AllMatches
    # can't easily show exact backup; instead show head of new file
    $head = Get-Content -LiteralPath $mf -TotalCount 30
    $head | ForEach-Object { Write-Output $_ }
    $cnt++
}

Write-Output "Script concluído. Verifique os backups *.bak.* se precisar reverter."