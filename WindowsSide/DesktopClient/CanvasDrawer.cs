using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Forms;
using System.Windows.Media;
using System.Windows.Shapes;

namespace DesktopClient
{
    public class CanvasDrawer
    {
        Canvas canvas;
        TextBlock gestureInfoTextblock;

        static private Polyline _crtPoly = null;
        static private Brush leftKeyBrush = new SolidColorBrush(Colors.Blue),
                             rightKeyBrush = new SolidColorBrush(Colors.OrangeRed),
                             noKeyBrush = new SolidColorBrush(Colors.DarkGray);
        static private float[] lastSegmentPoint = new float[2];
        private const int trailsThickness = 5;
        public CanvasDrawer(Canvas canvas, TextBlock gestureInfoTextblock)
        {
            this.canvas = canvas;
            this.gestureInfoTextblock = gestureInfoTextblock;
        }

        public void DrawOnlyPoint(float xPercent, float yPercent, bool? leftBtn)
        {   
            canvas.Dispatcher.Invoke(() => {
                Ellipse point = null;
                Brush fill = leftBtn.HasValue ? (leftBtn.Value ? leftKeyBrush : rightKeyBrush) : noKeyBrush;
            
                if (canvas.Children.Count != 1 || !(canvas.Children[0] is Ellipse))
                {
                    point = new Ellipse() { Width = trailsThickness * 2, Height = trailsThickness * 2, 
                                            Fill = fill };

                    canvas.Children.Clear();
                    canvas.Children.Add(point);
                }
                else {
                    point = (Ellipse)canvas.Children[0];
                    point.Fill = fill;
                }
                Canvas.SetLeft(point, canvas.ActualWidth * xPercent - trailsThickness);
                Canvas.SetTop(point, canvas.ActualHeight * yPercent - trailsThickness);
            });
        }

        public void BeginPolyline(float x, float y, bool startedByLeftBtn) 
        {
            lastSegmentPoint[0] = x;
            lastSegmentPoint[1] = y;

            canvas.Dispatcher.Invoke(() =>
            {
                _crtPoly = new Polyline() { Stroke = startedByLeftBtn ? leftKeyBrush : rightKeyBrush, StrokeThickness = trailsThickness };
                _crtPoly.Points.Add(new Point(canvas.ActualWidth * x, canvas.ActualHeight * y));
                canvas.Children.Add(_crtPoly);
            });
        }
        public void DrawPolylineSegment(float endX, float endY) 
        {
            canvas.Dispatcher.Invoke(() =>
            {
                if (canvas.Children.Count > 1)
                {
                    canvas.Children.Clear();
                    canvas.Children.Add(_crtPoly);
                }

                _crtPoly.Points.Add(new Point(canvas.ActualWidth * endX, canvas.ActualHeight * endY));
            });
        }

        public void WriteButtonGestureInfo(MouseCommands gesture, float x, float y, bool left)
        {
            int displayX = (int)Math.Round(x * Screen.PrimaryScreen.Bounds.Width),
                displayY = (int)Math.Round(y * Screen.PrimaryScreen.Bounds.Height);

            gestureInfoTextblock.Dispatcher.Invoke(() =>
            {
                gestureInfoTextblock.Text = $"{(left ? "Left" : "Right")} {gesture} at ({displayX}; {displayY})";
            });
        }

        public void WriteWheelScrollInfo(bool up)
        {
            gestureInfoTextblock.Dispatcher.Invoke(() =>
            {
                gestureInfoTextblock.Text = $"Wheel scroll {(up ? "up" : "down")}";
            });
        }
    }
}
