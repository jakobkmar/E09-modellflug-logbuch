<script setup lang="ts">
import { onMounted, ref } from 'vue'

defineProps<{
  width?: number
  height?: number
  styleWidth?: string
}>()

const canvasElement = ref<HTMLCanvasElement | null>(null)
let canvas: HTMLCanvasElement
let canvasCtx: CanvasRenderingContext2D

onMounted(() => {
  const tmpCanvas = canvasElement.value
  if (tmpCanvas == null) {
    console.warn("Could not find canvas element")
    return
  }
  const tmpCanvasCtx = tmpCanvas?.getContext("2d")
  if (tmpCanvasCtx == null) {
    console.warn("Could not get 2d context of canvas element")
    return
  }

  canvas = tmpCanvas
  canvasCtx = tmpCanvasCtx
})

let isDrawing = false
let x = 0
let y = 0

function convertX(x: number) {
  return (x / canvas.getBoundingClientRect().width) * canvas.width
}

function convertY(y: number) {
  return (y / canvas.getBoundingClientRect().height) * canvas.height
}

function drawLine(x1: number, y1: number, x2: number, y2: number) {
  canvasCtx.beginPath()
  canvasCtx.strokeStyle = 'black'
  canvasCtx.lineWidth = 5
  canvasCtx.moveTo(convertX(x1), convertY(y1))
  canvasCtx.lineTo(convertX(x2), convertY(y2))
  canvasCtx.stroke()
  canvasCtx.closePath()
}

function beginDrawingTouch(event: TouchEvent) {
  event.preventDefault()
  beginDrawing(event.targetTouches[0])
}
function beginDrawing(event: MouseEvent | Touch) {
  onMouseMove(event)
  isDrawing = true
}

function stopDrawingTouch(event: TouchEvent) {
  if (isDrawing) {
    event.preventDefault()
  }
  stopDrawing(event.changedTouches[0])
}
function stopDrawing(event: MouseEvent | Touch) {
  if (isDrawing) {
    onMouseMove(event)
    isDrawing = false
  }
}

function onTouchMove(event: TouchEvent) {
  event.preventDefault()
  onMouseMove(event.targetTouches[0])
}
function onMouseMove(event: MouseEvent | Touch) {
  const offsetX = event.pageX - canvas.offsetLeft;
  const offsetY = event.pageY - canvas.offsetTop;
  if (isDrawing) {
    drawLine(x, y, offsetX, offsetY)
  }
  x = offsetX;
  y = offsetY;
}

function getDataUrl() {
  return canvas.toDataURL()
}

function getImageData() {
  return canvasCtx.getImageData(0, 0, canvas.width, canvas.height).data
}

function isEmpty() {
  return getImageData().every((value) => value == 0)
}

defineExpose({ getDataUrl, getImageData, isEmpty })
</script>

<template>
  <canvas
    ref="canvasElement"
    :width="width" :height="height" :style="{ width: styleWidth }"
    @touchstart="beginDrawingTouch" @touchend="stopDrawingTouch" @touchcancel="stopDrawingTouch"
    @touchmove="onTouchMove"
    @mousedown="beginDrawing" @mouseup="stopDrawing" @mouseout="stopDrawing"
    @mousemove="onMouseMove" />
</template>
