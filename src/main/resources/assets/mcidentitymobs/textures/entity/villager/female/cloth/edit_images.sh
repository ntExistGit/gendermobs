#!/bin/bash

# chmod +x edit_images.sh
# ./edit_images.sh

# ================= НАСТРОЙКИ (редактируйте перед запуском) =================

# true = применять маски, false = не применять
ADD_ALPHA=false

# Файлы масок (grayscale, белый = непрозрачный, чёрный = прозрачный)
FIRST_ALPHA="D:/Games/Minecraft/gendermobs/src/main/resources/assets/mcidentitymobs/textures/entity/villager/maleSkin.png"
LAST_ALPHA="D:/Games/Minecraft/gendermobs/src/main/resources/assets/mcidentitymobs/textures/entity/villager/femaleSkin.png"

# Массив регионов для вырезания: "имя=ШиринаxВысота+X+Y"
EXTRACT_REGIONS=(
	#right_arm
    "right_arm_u=4x4+44+16"
    "right_arm_d=4x4+48+16"
    "right_arm_n=4x12+44+20"
    "right_arm_w=4x12+48+20"
    "right_arm_s=4x12+52+20"
	#right_sleeve
    "right_sleeve_u=4x4+44+32"
    "right_sleeve_d=4x4+48+32"
    "right_sleeve_n=4x12+44+36"
    "right_sleeve_w=4x12+48+36"
    "right_sleeve_s=4x12+52+36"
	#left_arm
    "left_arm_u=4x4+36+48"
    "left_arm_d=4x4+40+48"
    "left_arm_n=4x12+36+52"
    "left_arm_w=4x12+40+52"
    "left_arm_s=4x12+44+52"
	#left_sleeve
    "left_sleeve_u=4x4+52+48"
    "left_sleeve_d=4x4+56+48"
    "left_sleeve_n=4x12+52+52"
    "left_sleeve_w=4x12+56+52"
    "left_sleeve_s=4x12+60+52"
)

# Массив обрезки регионов: "имя операция=значение ..."
# Доступные операции: chop-left, chop-right, chop-top, chop-bottom,
#                     trim-left, trim-right, trim-top, trim-bottom (синонимы)
CROP_REGIONS=(
	#right_arm
    "right_arm_u chop-right=1"
    "right_arm_d chop-left=1"
    "right_arm_n chop-right=1"
    "right_arm_s chop-left=1"
	#right_sleeve
    "right_sleeve_u chop-right=1"
    "right_sleeve_d chop-left=1"
    "right_sleeve_n chop-right=1"
    "right_sleeve_s chop-left=1"
	#left_arm
    "left_arm_u chop-left=1"
    "left_arm_d chop-right=1"
    "left_arm_n chop-left=1"
    "left_arm_s chop-right=1"
	#left_sleeve
    "left_sleeve_u chop-left=1"
    "left_sleeve_d chop-right=1"
    "left_sleeve_n chop-left=1"
    "left_sleeve_s chop-right=1"
)

# Массив вклеивания регионов обратно: "имя=+X+Y"
COMPOSE_REGIONS=(
	#right_arm
    "right_arm_u=+44+16"
    "right_arm_d=+47+16"
    "right_arm_n=+44+20"
    "right_arm_w=+47+20"
    "right_arm_s=+51+20"
	#right_sleeve
    "right_sleeve_u=+44+32"
    "right_sleeve_d=+47+32"
    "right_sleeve_n=+44+36"
    "right_sleeve_w=+47+36"
    "right_sleeve_s=+51+36"
	#left_arm
    "left_arm_u=+36+48"
    "left_arm_d=+39+48"
    "left_arm_n=+36+52"
    "left_arm_w=+39+52"
    "left_arm_s=+43+52"
	#left_sleeve
    "left_sleeve_u=+52+48"
    "left_sleeve_d=+55+48"
    "left_sleeve_n=+52+52"
    "left_sleeve_w=+55+52"
    "left_sleeve_s=+59+52"
)

# Папки
INPUT_DIR="."               # где лежат исходные PNG
OUTPUT_DIR="./result"       # сюда сохранятся обработанные файлы
TMP_DIR="./temp"            # временная папка (автоматически удалится)

DO_STRIP=true               # true = удалять метаданные (EXIF и пр.)

# ================= ПРОВЕРКА IMAGEMAGICK =================

IM_CMD=""
if command -v magick &>/dev/null; then
    IM_CMD="magick"
elif command -v convert &>/dev/null; then
    if convert --version 2>&1 | grep -i "imagemagick" >/dev/null; then
        IM_CMD="convert"
    else
        echo "ОШИБКА: Найден системный convert Windows, а не ImageMagick."
        exit 1
    fi
else
    echo "ОШИБКА: ImageMagick не установлен."
    exit 1
fi

echo "ImageMagick найден: $IM_CMD"

# ================= ПРОВЕРКА И СОЗДАНИЕ ПАПОК =================

mkdir -p "$OUTPUT_DIR" 2>/dev/null || { echo "Не могу создать $OUTPUT_DIR"; exit 1; }
mkdir -p "$TMP_DIR"     2>/dev/null || { echo "Не могу создать $TMP_DIR"; exit 1; }

# ================= ПОДГОТОВКА МАСОК (только если ADD_ALPHA=true) =================

FIRST_ALPHA_STRIPPED=""
LAST_ALPHA_STRIPPED=""

if [ "$ADD_ALPHA" = true ]; then
    if [ -n "$FIRST_ALPHA" ] && [ -f "$FIRST_ALPHA" ]; then
        FIRST_ALPHA_STRIPPED="$TMP_DIR/first_alpha_stripped.png"
        $IM_CMD "$FIRST_ALPHA" -strip "$FIRST_ALPHA_STRIPPED"
        echo "Маска FIRST_ALPHA подготовлена"
    fi

    if [ -n "$LAST_ALPHA" ] && [ -f "$LAST_ALPHA" ]; then
        LAST_ALPHA_STRIPPED="$TMP_DIR/last_alpha_stripped.png"
        $IM_CMD "$LAST_ALPHA" -strip "$LAST_ALPHA_STRIPPED"
        echo "Маска LAST_ALPHA подготовлена"
    fi
else
    echo "Режим без масок (ADD_ALPHA=false)"
fi

# ================= ОСНОВНОЙ ЦИКЛ =================

for img in "$INPUT_DIR"/*.png; do
    [ -f "$img" ] || continue
    echo "▶ Обработка: $img"

    base=$(basename "$img" .png)
    workdir="$TMP_DIR/$base"
    mkdir -p "$workdir"

    # ---- Шаг 1: первая маска (если ADD_ALPHA=true) ----
    if [ "$ADD_ALPHA" = true ] && [ -n "$FIRST_ALPHA_STRIPPED" ]; then
        $IM_CMD "$img" "$FIRST_ALPHA_STRIPPED" -alpha off -compose CopyOpacity -composite -strip "$workdir/step.png"
    else
        $IM_CMD "$img" -strip "$workdir/step.png"
    fi

    # ---- Шаг 2: вырезание регионов с удалением из оригинала ----
    for region in "${EXTRACT_REGIONS[@]}"; do
        name="${region%%=*}"
        geom="${region#*=}"
        echo "  Вырезаем $name: $geom"

        # Сохраняем вырезанный регион
        $IM_CMD "$workdir/step.png" -crop "$geom" +repage -strip "$workdir/${name}.png"

        # Парсим геометрию для удаления области из оригинала
        if [[ $geom =~ ([0-9]+)x([0-9]+)\+([0-9]+)\+([0-9]+) ]]; then
            w=${BASH_REMATCH[1]}
            h=${BASH_REMATCH[2]}
            x=${BASH_REMATCH[3]}
            y=${BASH_REMATCH[4]}

            # Удаляем область: устанавливаем альфа-канал в 0 в этом прямоугольнике
            $IM_CMD "$workdir/step.png" \
                -region "${w}x${h}+${x}+${y}" \
                -alpha set -channel A -evaluate set 0% \
                +region \
                "$workdir/step_tmp.png"
            mv "$workdir/step_tmp.png" "$workdir/step.png"
        else
            echo "  ⚠ Не удалось разобрать геометрию '$geom' — пропускаем удаление"
        fi
    done

    # ---- Шаг 3: обрезка регионов (модификация вырезанных кусков) ----
    for crop_item in "${CROP_REGIONS[@]}"; do
        name="${crop_item%% *}"
        operations="${crop_item#* }"
        region_file="$workdir/${name}.png"
        if [ ! -f "$region_file" ]; then
            echo "  ⚠ Регион '$name' не найден – пропускаем обрезку"
            continue
        fi

        read w h <<< $($IM_CMD "$region_file" -format "%w %h" info:)
        if [ -z "$w" ] || [ -z "$h" ]; then
            echo "  ⚠ Не удалось определить размеры '$region_file'"
            continue
        fi

        left=0; right=0; top=0; bottom=0
        for op in $operations; do
            key="${op%%=*}"
            val="${op#*=}"
            case "$key" in
                chop-left|trim-left)   left=$((left + val)) ;;
                chop-right|trim-right) right=$((right + val)) ;;
                chop-top|trim-top)     top=$((top + val)) ;;
                chop-bottom|trim-bottom) bottom=$((bottom + val)) ;;
                *) echo "  ⚠ Неизвестная операция '$key' – игнорируем" ;;
            esac
        done

        new_w=$((w - left - right))
        new_h=$((h - top - bottom))
        if [ "$new_w" -le 0 ] || [ "$new_h" -le 0 ]; then
            echo "  ⚠ Обрезка региона '$name' даёт неположительный размер – пропускаем"
            continue
        fi

        $IM_CMD "$region_file" -crop "${new_w}x${new_h}+${left}+${top}" +repage "$region_file.tmp" &&
        mv "$region_file.tmp" "$region_file"
    done

    # ---- Шаг 4: вклеивание регионов обратно (по координатам из COMPOSE_REGIONS) ----
    current="$workdir/step.png"
    for compose_item in "${COMPOSE_REGIONS[@]}"; do
        name="${compose_item%%=*}"
        geom="${compose_item#*=}"
        region_file="$workdir/${name}.png"
        if [ ! -f "$region_file" ]; then
            echo "  ⚠ Регион '$name' не найден – пропускаем вклейку"
            continue
        fi

        echo "  Вклеиваем $name по координатам $geom"
        next="$workdir/step_tmp.png"
        $IM_CMD "$current" "$region_file" -geometry "$geom" -compose over -composite -strip "$next"
        mv "$next" "$current"
    done

    # ---- Шаг 5: финальная маска (если ADD_ALPHA=true) ----
    if [ "$ADD_ALPHA" = true ] && [ -n "$LAST_ALPHA_STRIPPED" ]; then
        $IM_CMD "$current" "$LAST_ALPHA_STRIPPED" -alpha off -compose CopyOpacity -composite -strip "$workdir/final.png"
    else
        mv "$current" "$workdir/final.png"
    fi

    # ---- Шаг 6: удаление метаданных и сохранение ----
    final="$workdir/final.png"
    output="$OUTPUT_DIR/$base.png"
    if [ "$DO_STRIP" = true ]; then
        $IM_CMD "$final" -strip "$output"
    else
        cp "$final" "$output"
    fi

    rm -rf "$workdir"
    echo "✅ Сохранено: $output"
done

# Удаляем временные копии масок
rm -f "$FIRST_ALPHA_STRIPPED" "$LAST_ALPHA_STRIPPED" 2>/dev/null

echo "🎉 Все изображения обработаны!"